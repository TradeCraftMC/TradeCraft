package dev.tradecraft.tradecraft.web.utils

import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.core.TreeNode
import dev.tradecraft.tradecraft.TradeCraft
import dev.tradecraft.tradecraft.database.objects.User
import dev.tradecraft.tradecraft.web.WebManager
import dev.tradecraft.tradecraft.web.abst.HttpResponse
import dev.tradecraft.tradecraft.web.abst.SimpleAPIResponse
import dev.tradecraft.tradecraft.web.abst.WebHandler
import dev.tradecraft.tradecraft.web.abst.WebRoutePrefix
import io.undertow.server.HttpServerExchange
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.net.InetAddress
import java.util.function.Consumer

abstract class RPCHandler<K, T>(val rpcInterface: Class<K>, val rpcObjectClass: Class<T>) : WebHandler where T : K {
    val functions = rpcInterface.declaredMethods.associateBy { it.name }
    val resourceName = rpcObjectClass.simpleName
    val prefix = this::class.java.getAnnotation(WebRoutePrefix::class.java).path
    val useValidator = rpcObjectClass.interfaces.find { it == RPCValidator::class.java } != null

    // Using other function
    override fun handle(request: HttpServerExchange, response: Consumer<HttpResponse?>, ip: InetAddress, user: User?) =
        null;

    override fun handle(
        request: HttpServerExchange, response: Consumer<HttpResponse?>, relativeUrl: String, ip: InetAddress,
        user: User?
    ) {
        request.requestReceiver.receiveFullBytes { exchange: HttpServerExchange, bytes: ByteArray ->
            val pathObjs = relativeUrl.substring(prefix.length).trim('/').split('/');
            val objectId = pathObjs.getOrNull(0);
            val commandName = pathObjs.getOrNull(1);

            if (objectId == null || commandName == null) return@receiveFullBytes response.accept(
                HttpResponse.createJSONResponse(
                    400, SimpleAPIResponse(400, "RPC requires an object ID and function name", success = false)
                )
            )
            val func = functions[commandName] ?: return@receiveFullBytes response.accept(
                HttpResponse.createJSONResponse(404, SimpleAPIResponse(404, "Invalid RPC procedure", false))
            );

            val rpcObject: T? = (TradeCraft.databaseManager.useDatabaseSession {
                it.createNativeQuery("select * from $resourceName where id = :id", rpcObjectClass)
                    .setParameter("id", objectId).resultList.getOrNull(0)
            } as T?)
            if (rpcObject == null) return@receiveFullBytes response.accept(
                HttpResponse.createJSONResponse(404, SimpleAPIResponse(404, "Invalid object ID for RPC", false))
            );

            if (useValidator) {
                val isValid = RPCValidator::validate.call(rpcObject, commandName, user)
                if (!isValid) return@receiveFullBytes response.accept(
                    HttpResponse.createJSONResponse(
                        403, SimpleAPIResponse(403, "Not authorized to execute this procedure", false)
                    )
                )
            }

            val requiredParams = func.parameters.map { Pair(it.name, it.type) }
            val params = mutableListOf<Any>()
            val webParameters: TreeNode? = WebManager.webReader.readTree(bytes)
            for ((param, paramType) in requiredParams) {
                val errorResponse =
                    HttpResponse.createJSONResponse(400, SimpleAPIResponse(400, "Requires parameter \"$param\"", false))
                if (webParameters == null) return@receiveFullBytes response.accept(errorResponse)

                val webParam: TreeNode =
                    webParameters.get(param) ?: return@receiveFullBytes response.accept(errorResponse);
                try {
                    val webParamObject = WebManager.webReader.readValue(webParam.traverse(), paramType);
                    params.add(webParamObject)
                } catch (e: Exception) {
                    e.printStackTrace()
                    return@receiveFullBytes response.accept(
                        HttpResponse.createJSONResponse(
                            400, SimpleAPIResponse(400, "Failed to parse parameter $param: \"${e.message}\"", false)
                        )
                    )
                }
            }

            return@receiveFullBytes try {
                val result = func.invoke(rpcObject, *params.toTypedArray());
                response.accept(HttpResponse.createJSONResponse(200, result))
            } catch (e: InvocationTargetException) {
                response.accept(
                    HttpResponse.createJSONResponse(
                        400, SimpleAPIResponse(400, "RPC returned an error: ${e.targetException.message}", false)
                    )
                )
            }
        }
    }
}
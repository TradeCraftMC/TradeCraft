package dev.tradecraft.tradecraft.web.utils

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import dev.tradecraft.tradecraft.TradeCraft
import dev.tradecraft.tradecraft.database.objects.User
import dev.tradecraft.tradecraft.web.WebManager
import dev.tradecraft.tradecraft.web.abst.HttpResponse
import dev.tradecraft.tradecraft.web.abst.SimpleAPIResponse
import dev.tradecraft.tradecraft.web.abst.WebHandler
import io.undertow.server.HttpServerExchange
import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.NoResultException
import java.net.InetAddress
import java.nio.charset.StandardCharsets
import java.util.function.Consumer
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

abstract class CrudHandler<T : Any>(kotlinType: KClass<out T>) : WebHandler {
    private val type: Class<out T> = kotlinType.java
    private val resourceName: String = type.simpleName
    private val fields = kotlinType.memberProperties.filterIsInstance<KMutableProperty<*>>()
    private val indexedFields = fields.associateBy { it.name }
    private val javaFields = type.declaredFields
    private val indexedJavaFields = javaFields.associateBy { it.name }

    abstract fun authenticate(user: User?, method: String): Boolean
    abstract fun checkBlacklist(user: User?, field: KMutableProperty<*>): Boolean

    private fun handleRead(request: HttpServerExchange, response: Consumer<HttpResponse?>) {
        val id = request.queryParameters["id"]
        if (id != null) {
            try {
                val value = TradeCraft.databaseManager.useDatabaseSession {
                    it.createNativeQuery("select * from $resourceName where id = :id", type)
                        .setParameter("id", id).singleResult
                }!!
                response.accept(
                    HttpResponse.createJSONResponse(200, value)
                )
            } catch (e: NoResultException) {
                response.accept(HttpResponse(404))
            }

        }

        val values = TradeCraft.databaseManager.useDatabaseSession {
            it.createNativeQuery("select * from $resourceName", type).resultList
        }

        response.accept(
            if (values == null) HttpResponse(404) else HttpResponse.createJSONResponse(200, values)
        );
        return;
    }

    private fun assignAndValidateProperties(
        json: JsonNode, obj: Any, user: User?, existingId: String? = null
    ): HttpResponse? {
        for ((key, jsonNode) in json.fields()) {
            val field = indexedFields[key]

            val invalidResponse = HttpResponse.createJSONResponse(
                400, SimpleAPIResponse(400, "Invalid parameter '$key'", false)
            );
            if (field == null) return invalidResponse;
            if (field.hasAnnotation<Id>()) return invalidResponse;
            if (!checkBlacklist(user, field)) return invalidResponse;

            val fieldClass = field.returnType.classifier as KClass<*>

            // Custom type handlers
            if (jsonNode.isTextual && fieldClass.java.isEnum) {
                val stringValue = jsonNode.asText();
                val enumConstants = fieldClass.java.enumConstants
                val enumConstant = enumConstants.find { it.toString() == stringValue }
                if (enumConstant == null) {
                    return invalidResponse
                }

                field.setter.call(obj, enumConstant)
                continue;
            }

            try {
                val value = WebManager.webReader.treeToValue(jsonNode, fieldClass.java);
                field.setter.call(obj, value);
            } catch (e: JsonProcessingException) {
                return invalidResponse
            } catch (e: IllegalArgumentException) {
                return invalidResponse
            }
        }

        if (obj is CrudValidator<*>) {
            val errorMessage = obj.validateAny(obj);
            if (errorMessage != null) {
                return HttpResponse.createJSONResponse(400, SimpleAPIResponse(400, errorMessage, false));
            }
        }

        val uniqueFieldMap: HashMap<String, Any?> = HashMap();
        for (field in fields) {
            val javaField = indexedJavaFields[field.name]!!
            val columnAnnotation: Column? = javaField.annotations.find {
                it is Column
            } as Column?
            if (columnAnnotation == null) continue

            if (columnAnnotation.unique) {
                val current = field.getter.call(obj)
                uniqueFieldMap[field.name] = current;
            }
        }

        // Everything under here generates a single query to check all unique fields
        // I can't find a way to lookup the intersection of columns between rows in SQL
        // so string concatenation it is
        var uniqueQuery = "select * from $resourceName where ";
        if (existingId != null) {
            uniqueQuery += "id <> :id and ";
        }
        uniqueQuery += "(";
        uniqueQuery += uniqueFieldMap.map { (key, _) -> "$key = :$key" }.joinToString(" or ")
        uniqueQuery += ")"

        val existing = TradeCraft.databaseManager.useDatabaseSession {
            val query = it.createNativeQuery(uniqueQuery, type)
            if (existingId != null) {
                query.setParameter("id", existingId)
            }
            for ((key, value) in uniqueFieldMap) {
                query.setParameter(key, value)
            }
            query.resultList
        }
        if (existing != null && existing.size > 0) {
            return HttpResponse.createJSONResponse(
                400, SimpleAPIResponse(400, "Entry is not unique.", false)
            )
        }


        return null
    }

    private fun handleCreate(request: HttpServerExchange, response: Consumer<HttpResponse?>, user: User?) {
        request.requestReceiver.receiveFullBytes { exchange: HttpServerExchange, bytes: ByteArray ->
            val stringData = String(bytes, StandardCharsets.UTF_8)
            val rawData = WebManager.webReader.readTree(stringData);

            val obj = type.getConstructor().newInstance()!!

            val error = assignAndValidateProperties(rawData, obj, user)
            if (error != null) {
                response.accept(error);
                return@receiveFullBytes;
            }

            TradeCraft.databaseManager.useDatabaseSession {
                it.persist(obj)
            }
            response.accept(HttpResponse.createJSONResponse(201, obj))
        }
    }

    private fun handleUpdate(request: HttpServerExchange, response: Consumer<HttpResponse?>, user: User?) {
        request.requestReceiver.receiveFullBytes { exchange: HttpServerExchange, bytes: ByteArray ->
            val stringData = String(bytes, StandardCharsets.UTF_8)
            val rawData = WebManager.webReader.readTree(stringData);

            val id = rawData.get("id")?.asText()
            if (id == null) {
                response.accept(
                    HttpResponse.createJSONResponse(400, SimpleAPIResponse(400, "Update requires ID", false))
                );
                return@receiveFullBytes
            }

            try {
                val obj = TradeCraft.databaseManager.useDatabaseSession {
                    it.createNativeQuery("select * from $resourceName where id = :id", type)
                        .setParameter("id", id).singleResult
                }!!

                (rawData as ObjectNode).remove("id");

                val error = assignAndValidateProperties(rawData, obj, user, id)
                if (error != null) {
                    response.accept(error);
                    return@receiveFullBytes;
                }

                val newObj = TradeCraft.databaseManager.useDatabaseSession {
                    it.merge(obj)
                }!!

                response.accept(HttpResponse.createJSONResponse(200, newObj))
            } catch (e: NoResultException) {
                response.accept(
                    HttpResponse.createJSONResponse(
                        404, SimpleAPIResponse(404, "Could not find object with ID '$id'", false)
                    )
                );

            }

        }
    }

    private fun handleDelete(request: HttpServerExchange, response: Consumer<HttpResponse?>) {
        request.requestReceiver.receiveFullBytes { exchange: HttpServerExchange, bytes: ByteArray ->
            val stringData = String(bytes, StandardCharsets.UTF_8)
            val rawData = WebManager.webReader.readValue<Map<String, Any>>(stringData);

            val id = rawData["id"]?.toString()
            if (id == null) {
                response.accept(
                    HttpResponse.createJSONResponse(400, SimpleAPIResponse(400, "Delete requires ID", false))
                );
                return@receiveFullBytes
            }

            val obj = TradeCraft.databaseManager.useDatabaseSession {
                val obj = it.get(type, id) ?: return@useDatabaseSession null;
                it.remove(obj)
            }

            if (obj == null) {
                response.accept(
                    HttpResponse.createJSONResponse(
                        404, SimpleAPIResponse(400, "Could not find object with ID '$id'", false)
                    )
                )
                return@receiveFullBytes;
            }

            response.accept(HttpResponse.createJSONResponse(200, obj))
            return@receiveFullBytes;
        }
    }

    override fun handle(
        request: HttpServerExchange, response: Consumer<HttpResponse?>, ip: InetAddress, user: User?
    ) {
        val allowed = authenticate(user, request.requestMethod.toString());
        if (!allowed) {
            response.accept(HttpResponse(403))
            return;
        }

        when (request.requestMethod.toString()) {
            "GET" -> return handleRead(request, response)
            "POST" -> return handleCreate(request, response, user)
            "PATCH" -> return handleUpdate(request, response, user)
            "DELETE" -> return handleDelete(request, response)
        }

        response.accept(HttpResponse(400))
        return;
    }
}
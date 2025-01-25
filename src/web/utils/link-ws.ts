import type { NuxtError } from "#app";

export type WebSocketCallback = (message: string) => void;
export type WebSocketErrorHandler = (error: NuxtError<unknown>) => void;

export class WebSocketHandler {
  private listeners: Array<WebSocketCallback> = [];

  private outQueue: Array<string> = [];
  private inQueue: Array<string> = [];

  private ws: WebSocket | undefined = undefined;
  private connected: boolean = false;

  private errorHandler: WebSocketErrorHandler | undefined = undefined;

  constructor(route: string, hostOverride = location.host) {
    if (import.meta.server) return;
    const isSecure = location.protocol === "https:";
    const url = (isSecure ? "wss://" : "ws://") + hostOverride + route;
    this.ws = new WebSocket(url);

    this.ws.onopen = () => {
      this.connected = true;
      for (const message of this.outQueue) {
        this.ws?.send(message);
      }
    };

    this.ws.onmessage = (e) => {
      const message = e.data;
      if (this.listeners.length == 0) {
        this.inQueue.push(message);
        return;
      }

      for (const listener of this.listeners) {
        listener(message);
      }
    };
  }

  error(handler: WebSocketErrorHandler) {
    this.errorHandler = handler;
  }

  listen(callback: WebSocketCallback) {
    this.listeners.push(callback);
    if (this.inQueue.length > 0) {
      for (const message of this.inQueue) {
        for (const listener of this.listeners) {
          listener(message);
        }
      }
    }
  }

  send(message: string) {
    if (!this.connected || !this.ws) {
      this.outQueue.push(message);
      return;
    }

    this.ws.send(message);
  }
}

export const linkWebsocketHandler = new WebSocketHandler(
  "/api/v1/auth/link-ws",
  "localhost:8080"
);

export default linkWebsocketHandler
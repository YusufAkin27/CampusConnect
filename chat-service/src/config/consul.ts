import Consul from "consul";
import { env } from "./env";
import { logger } from "../utils/logger";

const consul = new Consul({
  host: env.CONSUL_HOST,
  port: env.CONSUL_PORT
});

export async function registerService(): Promise<void> {
  const check = {
    http: `http://localhost:${env.SERVICE_PORT}/health`,
    interval: "10s"
  };

  const service = {
    id: env.SERVICE_ID,
    name: env.SERVICE_NAME,
    address: "localhost",
    port: env.SERVICE_PORT,
    check
  };

  await consul.agent.service.register(service);
  logger.info("Consul registration completed");
}

export async function deregisterService(): Promise<void> {
  try {
    await consul.agent.service.deregister(env.SERVICE_ID);
    logger.info("Consul deregistered");
  } catch (error) {
    logger.warn("Consul deregister failed", { error });
  }
}

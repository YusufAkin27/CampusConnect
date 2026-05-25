import { apiRequest, unwrapData } from "api/axiosClient";

export const getRequiredContracts = async () =>
  unwrapData(await apiRequest("/v1/api/contracts/required"));

export const getRequiredContractsFull = async () =>
  unwrapData(await apiRequest("/v1/api/contracts/required/full"));

export const getActiveContracts = async () =>
  unwrapData(await apiRequest("/v1/api/contracts/active"));

export const validateRequiredContracts = async (acceptedContractIds) =>
  unwrapData(
    await apiRequest("/v1/api/contracts/validate-required", {
      method: "POST",
      body: JSON.stringify({ acceptedContractIds }),
    })
  );

export const acceptContracts = async ({ userId, acceptedContractIds }) =>
  unwrapData(
    await apiRequest("/v1/api/contracts/accept", {
      method: "POST",
      body: JSON.stringify({ userId, acceptedContractIds }),
    })
  );

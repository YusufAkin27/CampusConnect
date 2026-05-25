import axiosInstance from './axiosInstance'
import { CONTRACT_ENDPOINTS } from '../shared/constants/apiConstants'

export const contractApi = {
  getActiveContracts: () => axiosInstance.get(CONTRACT_ENDPOINTS.ACTIVE),
  getRequiredContracts: () => axiosInstance.get(CONTRACT_ENDPOINTS.REQUIRED),
  validateRequiredContracts: (payload: { acceptedContractIds: string[] }) =>
    axiosInstance.post(CONTRACT_ENDPOINTS.VALIDATE_REQUIRED, payload),
  acceptContracts: (payload: { userId: string; acceptedContractIds: string[] }) =>
    axiosInstance.post(CONTRACT_ENDPOINTS.ACCEPT, payload),
  getContractDetail: (contractId: string) =>
    axiosInstance.get(CONTRACT_ENDPOINTS.ADMIN_DETAIL(contractId)),
}

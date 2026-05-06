# CampusConnect Chat Service

Node.js + TypeScript chat microservice for CampusConnect.

## Install

npm install

## Prisma

npx prisma generate
npx prisma migrate dev --name init_chat_service

## Run

npm run dev

## Health

GET http://localhost:8090/health

## REST Sample

POST http://localhost:8090/v1/api/chats/direct

## Socket Example

io("http://localhost:8090/chat", {
  auth: {
    token: "Bearer access_token"
  }
});

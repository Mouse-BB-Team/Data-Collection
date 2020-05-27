FROM node:12
WORKDIR /src/server

COPY package*.json ./
RUN npm install

COPY . .
EXPOSE 9090

ENV PORT=9090
ENV API_CLIENT_ID=client_id
ENV API_CLIENT_SECRET=password
ENV API_ROUTE_USER_CREATE=https://mouse-bb-api.herokuapp.com/user/create
ENV API_ROUTE_TOKEN_GRANT=https://mouse-bb-api.herokuapp.com/oauth/token
ENV API_ROUTE_TOKEN_CHECK=https://mouse-bb-api.herokuapp.com/oauth/check_token
ENV API_ROUTE_SESSION_ADD=https://mouse-bb-api.herokuapp.com/session/add
ENV REDIS_IP=127.0.0.1
ENV REDIS_PORT=6379

CMD [ "node", "src/server/server.start.js" ]

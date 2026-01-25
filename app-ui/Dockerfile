# Stage 1: Build
FROM node:20-alpine AS build
WORKDIR /app

# Copy dependency files first to leverage Docker layer caching
COPY package*.json ./

# Install dependencies including the specific animations package
# that the Angular 17+ Application Builder requires for resolution.
RUN npm install --legacy-peer-deps && \
    npm install @angular/animations --legacy-peer-deps

# Copy the rest of the application source code
COPY . .

# Run the production build
# Note: "dist/app-ui/browser" is the expected output for your builder
RUN npm run build -- --configuration=production

# Stage 2: Serve with Nginx
FROM nginx:alpine

# Copy the build output from Stage 1.
# Based on your angular.json, the project name is 'app-ui'
COPY --from=build /app/dist/app-ui/browser /usr/share/nginx/html

# Copy your custom nginx.conf to handle Angular SPAs (Single Page Applications)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]

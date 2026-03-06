# 🚀 Easy Apply Deployment Guide (DigitalOcean)

------------------------------------------------------------------------

# 🖥️ 1. Buy a Droplet

## Step 1: Log In

Go to **DigitalOcean → Log in**

## Step 2: Create Droplet

Click **Create → Droplets**

## Step 3: Choose Settings

-   **Image:** Ubuntu 22.04 LTS\
-   **Plan:** Basic\
-   **Size:** \$24/month (4GB RAM recommended)\
-   **Region:** Singapore (or closest to you)

------------------------------------------------------------------------

# 📦 2. Pull Source Code

SSH into your Droplet:

``` bash
ssh root@YOUR_DROPLET_IP
```

Clone your repository:

``` bash
git clone git@github.com:your-username/your-repo.git
cd your-repo
```

------------------------------------------------------------------------

# 🌐 3. Set Up Nginx (Reverse Proxy)

You will configure Nginx on your Ubuntu Droplet to:

-   Listen on **port 443 (HTTPS)**
-   Forward traffic to your backend running on **port 8443**

------------------------------------------------------------------------

## Install Nginx

``` bash
sudo apt update
sudo apt install nginx -y
```

------------------------------------------------------------------------

## Configure Nginx

Create configuration file:

``` bash
sudo nano /etc/nginx/sites-available/easy-apply.pro
```

Paste the following configuration:

``` nginx
server {
    listen 443 ssl;
    server_name easy-apply.pro;

    # SSL Configuration (Managed by Certbot)
    ssl_certificate /etc/letsencrypt/live/easy-apply.pro/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/easy-apply.pro/privkey.pem;

    location / {
        proxy_pass https://152.42.177.0:8443;
        proxy_ssl_verify off;
        proxy_ssl_server_name on;

        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}

server {
    listen 80;
    server_name easy-apply.pro;

    return 301 https://$host$request_uri;
}
```

------------------------------------------------------------------------

## Enable and Restart Nginx

``` bash
sudo ln -s /etc/nginx/sites-available/easy-apply.pro /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

------------------------------------------------------------------------

# 🔐 4. Install SSL Certificate (Let's Encrypt)

## Install Certbot

``` bash
sudo apt update
sudo apt install certbot python3-certbot-nginx -y
```

## Generate SSL Certificate

``` bash
sudo certbot --nginx -d easy-apply.pro
```

## Test Nginx Configuration

``` bash
sudo nginx -t
```

------------------------------------------------------------------------

# ✅ Deployment Complete

Your application is now accessible at:

    https://easy-apply.pro

------------------------------------------------------------------------

# 🏗️ Architecture Overview

    Internet → Nginx (443) → Spring Boot / Docker (8443)

------------------------------------------------------------------------

🎉 Your production deployment is ready!

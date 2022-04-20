package com.laisha.cargotransportservice.controller.command;

public class Router {

    public enum RouterType {

        FORWARD, REDIRECT
    }

    private String pagePath = PagePath.HOME;
    private RouterType routerType = RouterType.FORWARD;

    public Router(String pagePath) {

        if (pagePath != null) {
            this.pagePath = pagePath;
        }
    }

    public Router(String pagePath, RouterType routerType) {

        this(pagePath);
        if (routerType != null) {
            this.routerType = routerType;
        }
    }

    public String getPagePath() {
        return pagePath;
    }

    public RouterType getRouterType() {
        return routerType;
    }
}


app.service("loginShopService",function ($http) {
    this.loginName=()=>{
        return $http.get("../shop/loginName.do");
    }
})
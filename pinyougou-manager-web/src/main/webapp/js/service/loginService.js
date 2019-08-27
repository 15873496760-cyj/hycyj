//服务层
app.service('loginService',function($http){
    this.loginName=()=>{
        return $http.get("../login.do")
    }
})
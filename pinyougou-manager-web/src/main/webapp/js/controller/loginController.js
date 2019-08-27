app.controller('loginController',function($scope,$controller,loginService){
    $controller('baseController',{$scope:$scope});//继承

    $scope.loginName=()=>{
        loginService.loginName().success(response=>{
            $scope.name = response.name;
        })
    }

})
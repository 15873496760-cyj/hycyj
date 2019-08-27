app.controller("loginShopController",function ($scope,$controller,loginShopService) {
    //继承
    $controller("baseController",{$scope:$scope});

    $scope.loginName=()=>{
        loginShopService.loginName().success(response =>{
            $scope.name = response.name;
        })
    }
})
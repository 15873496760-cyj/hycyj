 //控制层 
app.controller('goodsController' ,function($scope,$location,$controller,typeTemplateService,itemCatService,uploadFileService,goodsService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){
        var id = $location.search()["id"];
        console.log(id);
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                editor.html($scope.entity.goodsDesc.introduction);
                $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);

                var items = $scope.entity.items;
                for (var i = 0,len = items.length; i < len; i++) {
                    items[i].spec = JSON.parse(items[i].spec);
                }
			}
		);
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象
		//得到富文本编辑的值
        $scope.entity.goodsDesc.introduction = editor.html();
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
                    $scope.entity = {};
                    editor.html("");
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

    //文件上传
    $scope.uploadFile=()=>{
        uploadFileService.uploadFile().success(response=>{
            if(response.success){
                //1。上传成功
                $scope.image_entity.url=response.message;
            }else{
                alert(response.message);
            }
        })
    }
    $scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};
    //将上传的文件对象添加到$scope.entity.goodsDesc.itemImages中
    $scope.addItemImages=()=>{
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }
    //删除图片
    $scope.removePic=(index)=>{
        $scope.entity.goodsDesc.itemImages.splice( index,1);
    }

    //根据父id查询数据
	$scope.findByParentId=(parentId)=>{
    	itemCatService.findByParentId(parentId).success(response=>{
            $scope.itemCatList = response;
		})
	}
    //时刻监听这entity.goods.category1Id的变化，然后进行调用方法
	$scope.$watch("entity.goods.category1Id",function(newValue,oldValue){
        itemCatService.findByParentId(newValue).success(response=>{
            $scope.itemCatList1 = response;
        })
	})
    //时刻监听这entity.goods.category2Id的变化，然后进行调用方法
    $scope.$watch("entity.goods.category2Id",function(newValue,oldValue){
        itemCatService.findByParentId(newValue).success(response=>{
            $scope.itemCatList2 = response;
        })
    })
	//时刻监听这entity.goods.category3Id的变化，然后进行调用方法
	$scope.$watch("entity.goods.category3Id",function (newValue,oldValue) {
		itemCatService.findOne(newValue).success(response=>{
            $scope.entity.goods.typeTemplateId = response.typeId;
		})
    })
    //时刻监听这entity.goods.typeTemplateId的变化，然后进行调用方法
	$scope.$watch("entity.goods.typeTemplateId",function (newValue,oldValue) {
		typeTemplateService.findOne(newValue).success(response=>{
			//通过typeid获取品牌的值
			$scope.typeTemplateList = JSON.parse(response.brandIds);
            if ($location.search()["id"] == null){
                //通过typeid获取模板的扩展属性
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems);
            }
		})
        typeTemplateService.findSpecByTypeId(newValue).success(response=>{
            $scope.specList = response;
		});
    })
    //举例如下：
    //Var list = [{“id”:”11”,”text”:”aaa”},{“id”:”12”,”text”:”bbb”}]
    //searchObjectByKey (list,”text”,”aaa”) 返回：{“id”:”11”,”text”:”aaa”}
    searchObjectByKey=(list,key,value)=>{
        for(var i = 0;i < list.length;i++){
            if(list[i][key] == value){
                return list[i];
            }
        }
        return null;
    }

    //默认选择
    $scope.chechedOptions=(specName,optionName)=>{
        var items = $scope.entity.goodsDesc.specificationItems;
        var options = searchObjectByKey(items,"attributeName",specName);
        if (options == null) {
            return false;
        } else {
            return options.attributeValue.indexOf(optionName) >= 0;
        }
    }
    $scope.updateSelectOption=(event,name,value)=>{
        //在$scope.entity.goodsDesc.specifictionItems查询指定的对象
        var obj = searchObjectByKey($scope.entity.goodsDesc.specificationItems,"attributeName",name);
        //判断对象是否存在
        if(obj){
            if(event.target.checked){
            	//如果勾选了某个规格选项的值
                obj.attributeValue.push(value);
            }else{
            	//如果未被复选，就从数组中删除它
                var index = obj.attributeValue.indexOf(value);
                obj.attributeValue.splice(index,1);
                //删除此选项后，再判断obj.attributeValue是否为null，如果是就从$scope.entity.goodsDesc.specifictionItems删除obj对象
                if(obj.attributeValue.length == 0){
                    //得到要删除的索引
                    var index1 = $scope.entity.goodsDesc.specificationItems.indexOf(obj);
                    //从$scope.entity.goodsDesc.specificationItems这个集合中删除此元素
                    $scope.entity.goodsDesc.specificationItems.splice(index1,1);
                }
            }
        }else{//2.2）如果不存在此对象，就将当前的值设置进这个对象中
            $scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
        }
        createItemList();
    }
    //生成sku商品列表
    createItemList=()=>{
        //1.先为sku商品列表赋初始值
        $scope.entity.items=[{spec:{},num:0,price:999,isDefault:0,status:0}];
        //2.获取规格列表
        var items = $scope.entity.goodsDesc.specificationItems;

        //3.遍历这个商品列表，动态生成新的商品列表
        for(var i = 0;i < items.length;i++){
            $scope.entity.items=addColumn($scope.entity.items,items[i].attributeName,items[i].attributeValue);
        }
    }
    //动态生成sku商品列表
    addColumn=(items, attributeName, attributeValue) =>{
        //1.定义一个空的数组用于存放动态生成的sku列表
        var newList = [];
        //2.遍历items，得到原来的数据，并根据原来的数据进行深克隆，目的是为了与原来的数据分开
        for(var i = 0;i < items.length;i++){
            //2.1)得到原来的旧的一行数据
            var oldRow = items[i];
            //2.2)根据旧的数据进行深克隆得到一个新行
            for(var j = 0; j < attributeValue.length;j++){
                //2.3)对原来的行进行深克隆
                var newRow = JSON.parse(JSON.stringify(oldRow));
                //2.4)对新行的spec属性进行赋值
                newRow.spec[attributeName]=attributeValue[j];
                //2.5)将新行添加到newlist数组中
                newList.push(newRow);
            }
        }
        // [{spec:{“网络”:”3G”},price:0,num:99999,status:'0',isDefault:'0' },
        // {spec:{“网络”:”4G”},price:0,num:99999,status:'0',isDefault:'0' }]
        return newList;
    }

    //定义状态数组
    $scope.status = ["未审核","已审核",'审核未通过',"已关闭"];
    //定义分类数组
    $scope.Categorys = [];
    $scope.findAllCategorys = ()=>{
        itemCatService.findAll().success(response=>{
            for (var i = 0,len = response.length; i < len; i++) {
                $scope.Categorys[response[i].id] = response[i].name;
            }
        })
    };

    //去修改
    $scope.toUpdate=(id)=>{
        location.href = "goods_edit.html#?id=" + id;
    }
});


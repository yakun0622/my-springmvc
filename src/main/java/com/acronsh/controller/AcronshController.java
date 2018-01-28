/*
* Copyright (c) 2017 Hinew. All Rights Reserved.
 * ============================================================================
 * 版权所有 海牛(上海)电子商务有限公司，并保留所有权利。
 * ----------------------------------------------------------------------------
 * ----------------------------------------------------------------------------
 * 官方网站：http://www.hinew.com.cn
 * ============================================================================
*/
package com.acronsh.controller;


import com.acronsh.annotation.Controller;
import com.acronsh.annotation.Qualify;
import com.acronsh.annotation.RequestMapping;
import com.acronsh.service.AcronshService;

/**
 * @author wangyakun
 * @email yakun0622@gmail.com
 * @date 2018/1/28 20:49
 */
@Controller
@RequestMapping("/acron")
public class AcronshController {
    @Qualify("acronshService")
    private AcronshService acronshService;

    @RequestMapping("/eat")
    public String eat(){
        System.out.println("Acron eat controller....");
        return "eat controller";
    }
}

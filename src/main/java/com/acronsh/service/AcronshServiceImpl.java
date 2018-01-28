/*
* Copyright (c) 2017 Hinew. All Rights Reserved.
 * ============================================================================
 * 版权所有 海牛(上海)电子商务有限公司，并保留所有权利。
 * ----------------------------------------------------------------------------
 * ----------------------------------------------------------------------------
 * 官方网站：http://www.hinew.com.cn
 * ============================================================================
*/
package com.acronsh.service;

import com.acronsh.annotation.Service;

/**
 * @author wangyakun
 * @email yakun0622@gmail.com
 * @date 2018/1/28 21:08
 */
@Service("acronshService")
public class AcronshServiceImpl implements AcronshService {
    public String eat() {
        System.out.println("eating....");
        return "Acron....";
    }
}

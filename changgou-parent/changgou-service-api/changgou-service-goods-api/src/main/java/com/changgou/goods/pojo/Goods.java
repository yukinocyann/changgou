package com.changgou.goods.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author ljh
 * @version 1.0
 * @date 2020/9/23 11:55
 * @description 标题
 * @package com.changgou.goods.pojo
 */
public class Goods implements Serializable {
    private Spu spu;
    private List<Sku> skuList;

    public Spu getSpu() {
        return spu;
    }

    public void setSpu(Spu spu) {
        this.spu = spu;
    }

    public List<Sku> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<Sku> skuList) {
        this.skuList = skuList;
    }
}

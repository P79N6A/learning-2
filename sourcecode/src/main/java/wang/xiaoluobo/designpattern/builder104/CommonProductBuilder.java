package wang.xiaoluobo.designpattern.builder104;

import com.alibaba.fastjson.JSON;

public class CommonProductBuilder extends Builder {

    private Product product = new Product();

    @Override
    protected Product build() {
        return product;
    }

    @Override
    protected void buildName() {
        product.setName("name");
    }

    @Override
    protected void buildType() {
        product.setType("type");
    }

    @Override
    protected void buildDesc() {
        product.setDesc("desc");
    }

    public static void main(String[] args) {
        CommonProductBuilder commonProductBuilder = new CommonProductBuilder();
        commonProductBuilder.buildName();
        commonProductBuilder.buildType();
        commonProductBuilder.buildDesc();
        Product product = commonProductBuilder.build();
        System.out.println(JSON.toJSONString(product));
    }
}

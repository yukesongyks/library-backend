package com.antfin.library.algorithm.model.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 哈希算法请求
 */
public class HashRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "输入文本不能为空")
    @Size(max = 10000, message = "输入文本长度不能超过10000")
    private String inputText;

    @Size(max = 16, message = "算法名称长度不能超过16")
    private String algorithm;

    public String getInputText() {
        return inputText;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
}

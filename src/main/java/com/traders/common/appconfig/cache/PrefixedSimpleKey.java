//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.traders.common.appconfig.cache;

import java.io.Serializable;
import java.util.Arrays;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class PrefixedSimpleKey implements Serializable {
    private final String prefix;
    private transient Object[] params;
    private final String methodName;
    private int hashCodeValue;

    public PrefixedSimpleKey(String prefix, String methodName, Object... elements) {
        Assert.notNull(prefix, "Prefix must not be null");
        Assert.notNull(elements, "Elements must not be null");
        this.prefix = prefix;
        this.methodName = methodName;
        this.params = new Object[elements.length];
        System.arraycopy(elements, 0, this.params, 0, elements.length);
        this.hashCodeValue = prefix.hashCode();
        this.hashCodeValue = 31 * this.hashCodeValue + methodName.hashCode();
        this.hashCodeValue = 31 * this.hashCodeValue + Arrays.deepHashCode(this.params);
    }

    public boolean equals(Object other) {
        return this == other || other instanceof PrefixedSimpleKey && this.prefix.equals(((PrefixedSimpleKey)other).prefix) && this.methodName.equals(((PrefixedSimpleKey)other).methodName) && Arrays.deepEquals(this.params, ((PrefixedSimpleKey)other).params);
    }

    public final int hashCode() {
        return this.hashCodeValue;
    }

    public String toString() {
        String var10000 = this.prefix;
        return var10000 + " " + this.getClass().getSimpleName() + this.methodName + " [" + StringUtils.arrayToCommaDelimitedString(this.params) + "]";
    }
}

# Extra

### Quick Start
#### Dependency
```
<dependency>
    <groupId>com.kangyonggan</groupId>
    <artifactId>extra-core</artifactId>
    <version>1.4</version>
</dependency>
```

#### Demo01.java
```
package com.kangyonggan.extra.test;

import com.kangyonggan.extra.core.annotation.Cache;

/**
 * @author kangyonggan
 * @since 2017/11/4 0004
 */
public class Demo01 {

    @Cache(key = "hello:${name}")
    public static String hello(String name) {
        if (name == null) {
            return name;
        }
        return name;
    }

}
```

#### Demo01.class
```
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kangyonggan.extra.test;

import com.kangyonggan.extra.core.handle.MemoryCacheHandle;

public class Demo01 {
    private static MemoryCacheHandle _memoryCacheHandle = new MemoryCacheHandle();

    public Demo01() {
    }

    public static String hello(String name) {
        Object _cacheValue = _memoryCacheHandle.get("hello:" + name);
        if (_cacheValue != null) {
            return (String)_memoryCacheHandle.set("hello:" + name, (String)_cacheValue, 9223372036854775807L);
        } else {
            return name == null ? (String)_memoryCacheHandle.set("hello:" + name, name, 9223372036854775807L) : (String)_memoryCacheHandle.set("hello:" + name, name, 9223372036854775807L);
        }
    }
} 
```

### extra.properties
`/resources/extra.properties`

```
## default value is ""
#cache.prefix=extra:
#
## default value is 315360000000 ms
#cache.expire=1800000
#
## default is com.kangyonggan.extra.core.handle.MemoryCacheHandle
#cache.handle=com.kangyonggan.extra.test.RedisCacheHandle
#
## default is com.kangyonggan.extra.core.handle.ConsoleLogHandle
#log.handle=com.kangyonggan.extra.test.Log4j2LogHandle
```

### Annotations
#### @Cache
1. Get value from cache, if not null return.
2. If cache value is null, do method and cache return value.

#### @CacheDel
1. Delete cache value.

#### @Log
1. Log the method input parameters.
2. Log the method return value.
3. Log the method used time.

### Code And Demo
[https://github.com/kangyonggan/extra.git](https://github.com/kangyonggan/extra.git)

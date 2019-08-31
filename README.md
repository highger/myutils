# myutils
该项目中维护了个人常用的一些工具类，可以帮助自己和大家在以后的项目更好的解决问题。


### id生成器：TransferNoGenerator
该项目中根据当前时间+redis维护一个自定义id生成器，简单易操作。

### 策略模式辅助类：AgencyApplicationContext
AgencyApplicationContext工具类通过获取Spring Bean中自定义注解，实现获取对应策略，来方便我们使用策略模式。

### 模版方法模式样板：TemplateTest
这个模版方法demo，主要用于输出数据，已对异常做了处理，并对任务执行时间做了统计。
其实在工作项目中，还可以基于注解结合策略模式，实现模版的选择，可更加简化我们项目中的代码。新增需求只需要实现定制化修改即可，增强了代码的可维护性，加速我们系统开发的进度。

### Excel操作类：ExcelHelper
该项目中通过ExcelHelper来实现Excel导出，通过自定义注解@ExcelBean、@ExcelColumn+反射+POI实现Excel导出。

### 时间转换类：DateConverter
该项目中通过DateConverter工具类实现LocalDateTime、LocalDate与Date之间的相互转化。

### Bean copy工具类：BeanCopierUtils
BeanCopierUtils通过反射技术实现bean字段拷贝，避免代码中大量的get/set。

### 时间统计类：TimerUtil
项目中我们需要统计代码执行时间，可通过该类辅助完成。也可自定义输出精度，最大精度为纳秒。只需通过构造方法来指定，并通过toString即可获得。

### 字符串解析工具类：JoinerUtils&SplitterUtils
项目中如果我们需要对固定格式字符串进行解析，我们可以通过这个工具类完成，该工具借助谷歌guava工具类Joiner&Splitter来实现的。
例如：
```
String nameList="张三,李四,王二,麻子";
List<String> names = SplitterUtils.COMMA.splitToList(nameList);
System.out.println(names);
输出：
[张三, 李四, 王二, 麻子]
```

### Lombok 

通过注解省略getter/setter,构造函数,toString.

极大地减少垃圾代码量.  [文档](http://jnb.ociweb.com/jnb/jnbJan2010.html)

### JSON 

- FastJson 中文文档优秀 [文档](https://github.com/Alibaba/fastjson/wiki/首页)
- Jackson 国外项目首选,客制化配置更多,三方库更多 [简易教程](https://www.mkyong.com/java/jackson-2-convert-java-object-to-from-json/)

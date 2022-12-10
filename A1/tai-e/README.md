# T1

## Test
所有单元测试
```shell script
gradlew clean test
```
针对指定类的测试命令
```shell script
gradlew run --args="-cp src/test/resources/dataflow/livevar  -m Assign"
```

## 思考及遇到的问题
- 一开始卡在对**访问者模式**的研究中了，以为可以通过`L/RValue`的`visit`方法得到对`Var`变量，太憨了！！！后续直接是通过`instanceof`来判断，然后强制转换
- 忽略了任务书给出的对于`cfg`遍历节点的方法，而是自己通过队列实现的后向遍历，对于简单样例没有问题，但是几个偏高级的测试过不了。后面采用给的基于`Graph`的迭代，代码量大大减少
- `null`错误，根据报出的定位信息加了判断，但是为什么会引起还没想通
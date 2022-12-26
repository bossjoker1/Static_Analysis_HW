# T3
## Test
所有单元测试
```shell script
gradlew clean test
```
针对指定类的测试命令

```shell script
gradlew run --args="-cp src/test/resources/dataflow/deadcode  -m ControlFlowUnreachable"
```
## 思考及遇到的问题
- 有点难，感觉被开始入口分析的时机卡住了，所以这里借鉴了下别人的，但其实代码量很小，而且就涉及到三个判断
- 首先我们将思路放在得到活跃代码上，先将死代码集合赋值为所有代码全集，之后再remove掉活跃代码即可
- 对于已经分析过的活跃代码不用再次分析
- 要顺着cfg将后续的nodes加入到待分析的队列中

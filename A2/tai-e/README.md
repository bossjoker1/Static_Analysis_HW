# T2

## Test
所有单元测试
```shell script
gradlew clean test
```
针对指定类的测试命令

```shell script
gradlew run --args="-cp src/test/resources/dataflow/constprop  -m BranchConstant"
```


## 思考及遇到的问题
- 看提示首先针对`newBoundaryFact()`方法，因为我们目前分析的都是过程内的，
所以通过前面的IR分析能得到所有的参数即变量名。且又因为是must analysis，所以得先将所有变量都视为NAC(最安全)。
另外还提示我们只需要分析整型变量，并且提供了一个方法来判断，需要在该函数里面要用到。其他node的OUT都先初始化为空(一开始没有什么信息)。
- 在做的过程中遇到的其他问题感觉与T1类似，一开始对着slides把`meetValue`方法都写错了，导致结果mismatch。这个比较好定位，因为错误就只可能在这个函数。
`WorkListSolver`蛮简单，因为我在T1一开始也是用队列写的。不得不再次夸赞Java的类型推导。感觉自己对OOP语言更加地喜爱了嘿嘿。
- 记得看github上关掉的
[issues](https://github.com/pascal-lab/Tai-e-assignments/issues/2) ，不然有一个testcase过不去。

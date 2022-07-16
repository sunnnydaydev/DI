# Dagger子组件

可以理解为父组件为父类，子组件就是子类。因此子组件可以使用父组件中的对象，除此之外我感觉子组件的另外一个好处就是方便单独管理某些对象的生命周期。

总结到这里可以简单使用一张图来概括下目前对Dagger的认识：

![NewGet](https://gitee.com/sunnnydaydev/my-pictures/raw/master/github/di/newget.png)

一般来说Module之间的对象可以相互调用，对象1可以使用Module的对象。子组件可以使用父组件的对象。


首先看一个关系图梳理：

![1](https://gitee.com/sunnnydaydev/my-pictures/raw/master/github/di/1.png)

如上图，是结合[登录栗子手动依赖注入](2、登录栗子手动依赖项注入.md)来使用Dagger自动依赖注入进行了改写。在改写过程中可能会涉及到如下：

- 子组件的使用：如何使用子组件
- 子组件的生命周期：组件生命周期在子组件上的应用、注意点。

本章节就把这些一块综合下。




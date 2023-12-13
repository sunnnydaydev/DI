# Dagger子组件

###### 1、Dagger module总结

（1）尽量让模块只在组件中声明一次。

如下ApplicationComponent 包括 Module1 和 Module2，Module1 包括 ModuleX。

```kotlin
@Component(modules = [Module1::class, Module2::class])
interface ApplicationComponent

@Module(includes = [ModuleX::class])
class Module1

@Module
class Module2
```

如果 Module2 现在依赖于 ModuleX 提供的类。错误做法是将 ModuleX 包含在 Module2 中，因为这样 ModuleX 在图中就出现了两次：
```kotlin
// Bad practice: ModuleX is declared multiple times in this Dagger graph
@Component(modules = [Module1::class, Module2::class])
interface ApplicationComponent

@Module(includes = [ModuleX::class])
class Module1

@Module(includes = [ModuleX::class])
class Module2
```

针对如上则一般有两种做法：

- 重构模块，并将共同模块提取到容器中。因为容器下的module之间可以相互访问的，所以吧ModuleX直接提取到容器中即可，让容器管理。

```kotlin
@Component(modules = [Module1::class, Module2::class, ModuleX::class])
interface ApplicationComponent

@Module
class Module1

@Module
class Module2
```

- 将 ModuleX 中 Module1 和 Module2 的共同依赖项提取到 ModuleXCommon 新模块中，然后，使用特定于每个模块的依赖项创建名为 ModuleXWithModule1Dependencies 和 ModuleXWithModule2Dependencies 的另外两个模块

```kotlin
@Component(modules = [Module1::class, Module2::class, ModuleXCommon::class])
interface ApplicationComponent

@Module
class ModuleXCommon 

@Module
class ModuleXWithModule1SpecificDependencies

@Module
class ModuleXWithModule2SpecificDependencies 

@Module(includes = [ModuleXWithModule1SpecificDependencies::class])
class Module1

@Module(includes = [ModuleXWithModule2SpecificDependencies::class])
class Module2
```



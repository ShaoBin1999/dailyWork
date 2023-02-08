#ServiceLoader

约束
- 服务类必须实现接口
- 服务类必须有无参构造器
- 配置文件需要使用UTF-8编码

特点
- 懒加载
- ServiceLoader使用LinkedHashMap缓存创建的服务实现类实例，LinkedHashMap在二次迭代时会按照Map#put执行顺序遍历
- 当存在多个提供者时，服务消费者模块不一定要全部使用， 而是需要根据某些特性筛选一种最佳实现。ServiceLoader的机制只能在遍历整个迭代器的过程中，从发现的实现类中决策出一个最佳实现
- 没有提供服务的注销机制
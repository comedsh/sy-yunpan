first v0.1 versions

check ClientTest cases for the client start scenarios, and see the TestUtils#makeupTestCases1 to notice that some sync needs more than 2 steps to complete.

of server, check the repository.properties for the path configured.

隐藏文件可以同步，(TODO) 但是一些系统自动生成的隐藏文件应该过滤掉，比如 mac 的 .DS_store 等

注意对 FileDifferenceImpl1 重构，经过算法的改良，性能提升了接近 30 倍。 


first v0.1 versions

1. check ClientTest cases for the client start scenarios, and see the TestUtils#makeupTestCases1 to notice that some sync needs more than 2 steps to complete.

2. of server, check the repository.properties for the path configured.

3. 隐藏文件可以同步，(TODO) 但是一些系统自动生成的隐藏文件应该过滤掉，比如 mac 的 .DS_store 等

4. 注意对 FileDifferenceImpl1 重构，经过算法的改良，性能提升了接近 30 倍。 

5. 特别注意，如果要导出 sy-yunpan-server 中的相应内容，记得一定要先 build sy-yunpan-server 是它生成 /build/classes/main classes 文件. 见 sy-yunpan-client/build.gradle


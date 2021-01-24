package com.demo._9nameservice;

/**
 * 测试主键生成器
 *
 * @author jerome
 * @date 2016/8/27 9:44
 */
public class TestIdMasker {

    public static void main(String[] args) throws Exception {

        IdMaker idMaker = new IdMaker("192.168.10.5:2181", "/NameService/IdGen", "ID");
        idMaker.start();

        try {
            for (int i = 0; i < 10; i++) {
                String id = idMaker.generateId(RemoveMethodEnum.DELAY);
                System.out.println(id);
            }
        } finally {
            idMaker.stop();
        }
    }

    /* console:
    0000000041
    0000000042
    0000000043
    0000000044
    0000000045
    0000000046
    0000000047
    0000000048
    0000000049
    0000000050
    */
}

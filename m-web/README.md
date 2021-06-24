# s-exchange-grabber

## summary

a service which crawl from web.

- 抓取twitter最新消息
    - 获取twitter url
        - defi
            - 定时获取
            - 从coingecko获取defi排名前100的币种
            - 并行取出coingecko中对应的twitter地址
                - 保存 <coinName, twitterurl> map
                - 只保存未保存的
                - 插入mongodb.coininfo
                
        - 自定义对象
            - 配置文件中读取
    
        - 去重
            
    - 获取最新消息
        - 从coininfo按更新时间取出最新的信息
        - 定时并行查询是否有新的消息
        - 保存消息并推送

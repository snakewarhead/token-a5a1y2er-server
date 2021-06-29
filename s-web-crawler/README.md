# s-exchange-grabber

## summary

a tool which crawl from web.

## run

- actions
    - gecko_twitter_crawl
        - param0, coin category, 1 - defi
        - param1, number of ranks
        
    - twitter_news_crawl

```cmd
java -jar s-web-crawler-1.0.0.jar --thread=4 --action=gecko_twitter_crawl --params=1 --params=100
java -jar s-web-crawler-1.0.0.jar --thread=4 --action=twitter_news_crawl --notices=xxx1@qq.com --notices=xxx2@qq.com
```

# encoding: utf-8

SLICENUM = 30

import requests, json

stockidlist_url = 'https://www.joudou.com/stockinfogate/stockidlist'

stockid2stockname_url = 'https://test.joudou.com/stockinfogate/dataapi/stocknames'

stockids_resp = requests.get(stockidlist_url).text

stockids = json.loads(stockids_resp)['data']

# 切分数组
def group(lst, n):
    num = len(lst) % n
    zipped = zip(*[iter(lst)] * n)
    return zipped if not num else zipped + [lst[-num:], ]

querys_stockids = [','.join(lst) for lst in group(stockids,SLICENUM)]

querys_param = [{"secucodes": stockids} for stockids in querys_stockids]

# print querys_param

# stockids

## 指定时间
http://index.haosou.com/index/indexqueryhour?q=300498,300182&t=7
## 指数概况
http://index.haosou.com/index/overviewJson?q=300182
## 搜索指数趋势
http://index.haosou.com/index/soIndexJson?q=300182
## 媒体关注度
http://index.haosou.com/index/soMediaJson?q=300182

# stocknames

stocknames_url = stockid2stockname_url

stocknames_resp = [requests.post(stocknames_url, data=query_param).text for query_param in querys_param]
#stocknames_resp = requests.post(stocknames_url, data=querys_param[1]).text

stocknames = []

for x in stocknames_resp:
    stocknames += json.loads(x)


## 指数概况

## 搜索指数趋势

## 媒体关注度

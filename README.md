# BloomFilter
 A Java implementation of Bloom filter

## 觀念簡介

布隆篩選器的優勢在於時間複雜度與空間複雜度:    
搜尋算法的時間複雜度與空間複雜度僅有*O(1)、既不占空間又省時*，當篩選清單龐大，有巨大的優勢。  
然而，此類篩選器是有誤報機率的。因此多應用於快速篩選、或是可容許偶爾失誤的場景，例如

* 垃圾郵件篩選器，符合關鍵字的郵件歸類於垃圾信件夾
* 報名網站的連接白名單，過濾大量瀏覽請求的同時、仍允許付款請求

## Java實作
本實作案例使用 java.security.MessageDigest 庫進行加密演算、以java.util.BitSet實作data bit array。

測試程式寫在 main，隨機生成1000個資料放入篩選器、並作1000次測試，計算正確率與誤報率。

## 誤報率分析

定義  
> n : 資料數目  
> m: bit 陣列長度  
> k: 雜湊函式數量  
根據公式可知，最小的誤報率p發生在以下條件  


>  p &ge; 0.5<sup>k</sup> 若 k = ln2(m/n)


本實作範例的規格為  
> n = 1000  
> m = 65536  
> k = 3  
> p = 2.42E-7 (1 in 4,134,560)  

雖然最佳的k值為42，不過3個雜湊函式已可保證百萬分之一以下的誤報率。

## 參考資料:
[hohshencode 2023](https://ithelp.ithome.com.tw/articles/10335958)  
[Kadai 2018](https://medium.com/@Kadai/資料結構大便當-bloom-filter-58b0320a346d)  
[calculator](https://hur.st/bloomfilter/?n=1000&p=&m=60KB&k=3) 
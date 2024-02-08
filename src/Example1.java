
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

/**
 * Bloom filter: 有微小偽陽性、而無偽陰性的篩選器，搜尋算法的時間複雜度為O(1)、空間複雜度O(1) 
 * 使用1個bit array，資料經過k個加密函式成為k個資料點紀錄(指紋)
 * 紀錄的資料數目定為n。
 * 資料紀錄於bit array 上，bit array 長度為m。
 * p = 誤報機率。
 * @author g7user
 *
 */
public class Example1 {
	
	private static final String ALGORITHM = "SHA-256";
	private static final int ARRAY_SIZE_IN_BYTE = 2; // m = 2^8*2 = 2^16 = 65536
	private static final String[] SALTS = new String[] {"SALT", "SBLT","SCLT"}; // 以鹽值區分為不同的加密函式 k = 3
	
	private BitSet hashData = new BitSet();
	private BitSet hashTest = null;
	
	/**
	 * Bloom filter
	 */
	public Example1() {}
	

	// 測試布隆篩選器
	public static void main(String[] args) {		
		// Bloom Filter
		Example1 ex1 = new Example1();
		// n = add 次數
		int dataSize = 1000; 
		// 測試次數
		int testCases = 10000; 
		// 生成隨機字串
		final String charList = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789"; // 
		Random random = new Random();
		// 檢查正確答案的列表
		List<String> checkList = new ArrayList<>();
		// 檢核計數
		int correctness = 0; // true_positive + true_negative
		int falsePositive = 0;
		
		// 加入5字元資料 
		for(int a = 0; a < dataSize; a++) {
			String data = "";
			for(int b = 0; b < 5; b++) {
				int index = random.nextInt(charList.length());
				data += charList.charAt(index);
			}
			ex1.add(data);
			checkList.add(data);
		}		
//		System.out.println("set count: "+exc8.getHashData().cardinality()); // #data*#hashFunc		
		// 生成5字元資料測試
		for(int c = 0; c < testCases; c++) {
			String test = "";
			for(int d = 0; d < 5; d++) {
				int index = random.nextInt(charList.length());
				test += charList.charAt(index);
			}
			
			if(ex1.contains(test) && checkList.contains(test)) correctness++;
			else if (!ex1.contains(test) && !checkList.contains(test)) correctness++;
			else if (ex1.contains(test) && !checkList.contains(test)) falsePositive++;
			
		}
		// 統計
		System.out.println(String.format("正確次數: %d 在  %d 中", correctness , testCases));
		System.out.println(String.format("正確率: %.2f%%", 100.0 * correctness / testCases));
		System.out.println(String.format("誤報次數: %d 在  %d 中", falsePositive , testCases));
		System.out.println(String.format("誤報率: %.2f%%", 100.0 * falsePositive / testCases));
		
	}
	
	/**
	 * 數據加入資料陣列
	 * @param data
	 */
	public void add(String data) {
		byte[] dataBytes = data.getBytes();
		for(String salt : SALTS) {
			hash(dataBytes, salt.getBytes(), true);
		}
	}
	
	/**
	 * Getter of hash data
	 * @return hash data
	 */
	public BitSet getHashData() {
		return this.hashData;
	}
	
	/**
	 * 數據與資料陣列比較
	 * @param test 測試數據
	 * @return true: 有此資料; false: 無此資料
	 */
	public boolean contains(String test) {
		byte[] dataBytes = test.getBytes();
		this.hashTest = new BitSet(); // 初始化
		for(String salt : SALTS) {
			hash(dataBytes, salt.getBytes(), false);
		}
		hashTest.and(hashData);		
		return ( hashTest.cardinality() == SALTS.length );
	}
	

	/**
	 * 加密函式
	 * @param input 加密字串bits
	 * @param salt 加鹽字串bits
	 * @param isAdding True: 更新資料陣列; False: 更新臨時陣列 
	 */
	public void hash(byte[] input, byte[] salt, boolean isAdding) {
		MessageDigest messageDigest;		
		try {
			messageDigest = MessageDigest.getInstance(ALGORITHM);
			messageDigest.update(salt);
			byte[] hashArray = messageDigest.digest(input); // hash value = 32 byte or 256-bit big-integer
			int hashNumber = 0;
			// 計算 hashValue % m。因為 lg m 為整數，取末位即是模數
			for(int i = hashArray.length - ARRAY_SIZE_IN_BYTE; i < hashArray.length; i++) {
				int b = hashArray[i] & 0xff;
				hashNumber = (hashNumber << 8) + b; // byte 轉 bit
			}
//			System.out.println();
//			System.out.println("hased tail: "+hashNumber);			
			if(isAdding) {				
				hashData.set(hashNumber);
			} else {
				hashTest.set(hashNumber);
			}
		} catch (Exception e) {
			// 若 ALGORITHM 非已知的雜湊算法，則拋出錯誤
			e.printStackTrace();
		}				
	}

}

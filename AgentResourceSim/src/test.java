
public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String cellData = "N42 16.921 W87 56.763";
//		System.out.println(cellData.substring(cellData.indexOf("W") + 1, cellData.indexOf("W") + 3));
		
		String cellData = "N42 16.921 & &W87 56.7&63";
		System.out.println(cellData.replaceAll("&", "AND"));
//		int a = 9;
//		for (double i = 0; i < a; i+=(double)a/10) {
//			System.out.println(i);
//		}
	}

}

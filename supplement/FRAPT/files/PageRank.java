package unsupervise;

import info.SimiDS;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;


import utility.SimilarityCal;


public class PageRank {
		
	
	public static String prraw="pagerank\\rawdata.txt";
	public static String afterprocess="pagerank\\afterprocess.txt";
	public static String aftertranspose="pagerank\\aftertranspose.txt";
	public static String finalmatrix="pagerank\\finalmatrix.txt";
	public static String prvalue="pagerank\\prvalue50.txt";
	public static float alpha=0.85f;
	public static int cycletime=200;
	
	
	
	public static ArrayList<Float> calPageRank(String projectid, int segmentid,Vector<String> allsentence,String project) throws Exception
	{

		Hashtable<String,Float> idf=new Hashtable<String,Float>();
		BufferedReader br=new BufferedReader(new FileReader(""));  //speficy the idf file
		String s="";
		while((s=br.readLine())!=null)
		{
			String key=s.substring(0, s.indexOf(","));
			float value=Float.parseFloat(s.substring(s.indexOf(",")+1, s.length()));
			idf.put(key, value);
		}
		br.close();
		
				
		Vector<SimiDS> allsimi=new Vector<SimiDS>();
		for(int i=0;i<allsentence.size()-1;i++)
		{
			for(int j=i+1;j<allsentence.size();j++)
			{
				
				float simi=0f;
				simi=SimilarityCal.calVSMSimi(allsentence.get(i), allsentence.get(j),idf);
				if(Float.isNaN(simi))
					simi=0.0f;
				SimiDS onesimi=new SimiDS(i,j,simi);
				allsimi.add(onesimi);
				SimiDS anothersimi=new SimiDS(j,i,simi);
				allsimi.add(anothersimi);
			}
		}
		

		
		findreference(allsentence,allsimi);
		matrixtranspro(allsentence);
		matrixtranspose(allsentence);
		makefinalmatrix(allsentence);
		ArrayList<Float> PageRankValue=prvalue(allsentence);
		System.out.println(PageRankValue.toString());
		return PageRankValue;
		
	}
	
	
	

	 public static void findreference(Vector<String> allsentence, Vector<SimiDS> allsimi) throws IOException
	 {
		
         
	     File ff = new File(prraw);  
		 BufferedWriter output = new BufferedWriter(new FileWriter(ff));		 
		 for(int temp=0;temp<allsentence.size();temp++)
		 {
			 
			 for(int temp1=0;temp1<allsentence.size();temp1++)
			 {
				 if(temp==temp1)
				 {
					 output.write("0.0  ");
				 }
				 else
				 {
					 for(SimiDS onesimi:allsimi)
					 {
						 if(onesimi.getId1()==temp&&onesimi.getId2()==temp1)
						 {
							 output.write(onesimi.getSimi()+"  ");
							 break;
						 }
					 }
				 }
			 }
			
			
			 output.newLine();
		 }
		 output.close();
	     
	     
	     
	 }
	 
	 
	 public static void matrixtranspro(Vector<String> allsentence) throws IOException
	 {
		 
		 BufferedReader brrr = new BufferedReader(new FileReader(prraw));
		 String sttr = "";
		 int id1=0;
		 Hashtable<Integer,Float> summap=new Hashtable<Integer,Float>();
		 while((sttr=brrr.readLine()) != null) 
		 {
			 float sum=0;
			 StringTokenizer st = new StringTokenizer(sttr);
			 while(st.hasMoreElements())
			 {
				float temp= Float.parseFloat(st.nextElement().toString());
				sum+=temp;
			 }

            summap.put(id1, sum);
			id1++;
		 }
		 
		 File ff = new File(afterprocess);  
		 BufferedWriter output = new BufferedWriter(new FileWriter(ff));		 				  
     	 
	     int id=0; 
		 BufferedReader br = new BufferedReader(new FileReader(prraw));
		 String str = "";
		 while((str=br.readLine()) != null) 
		 {
		    
		   float refnum=summap.get(id);
		 
			if(refnum!=0f)	
			{
				
				StringTokenizer st = new StringTokenizer(str);
				 while(st.hasMoreElements())
				 {
					float temp= Float.parseFloat(st.nextElement().toString());
					if(temp==0.0)
					{
					  output.write("0.0  ")	;
					}
					else 
					{
						float newvalue=(float) (temp/refnum);
						//System.out.print(newvalue+"  ");
						output.write(newvalue+"  ");
					}
			
				 }
			}
			
			else if(refnum==0)
			{
				
				
				
				StringTokenizer st = new StringTokenizer(str);
				 while(st.hasMoreElements())
				 {
					float temp= Float.parseFloat(st.nextElement().toString());
					if(temp==0.0)
					{
						float average=(float) (1.0/allsentence.size());
						output.write(average+"  ");
					}
					
					else  
					{
						System.out.println("wrong in "+id);
					}
				 }
			}
			
			id++;
			output.newLine();
//			System.out.println(id);
		 }
		 
		 output.close();
		 br.close();

	 }
	 
	
	 
	 
	 public static void matrixtranspose(Vector<String> allsentence) throws IOException
	 {
		
		 float[][] ma=new float[allsentence.size()][allsentence.size()];
		 BufferedReader br = new BufferedReader(new FileReader(afterprocess));
		 String str = "";
		 int line=0;
		 while((str=br.readLine()) != null) 
		 {
	 
			 StringTokenizer st = new StringTokenizer(str);
			 int colomn=0;
			 while(st.hasMoreElements())
			 {
				float temp= Float.parseFloat(st.nextElement().toString());
				ma[line][colomn]=temp;
				colomn++;
			 }
			 line++;
			 
		 }
		 br.close();
		 BufferedWriter bw = new BufferedWriter(new FileWriter(aftertranspose));
		 for(int i=0;i<allsentence.size();i++)
		 {
			 for(int j=0;j<allsentence.size();j++)
				 bw.write(ma[j][i]+"  ");
			 bw.newLine();	 			 
		 }
		 
		 bw.close();
		 
	 }
	 
	 public static void makefinalmatrix(Vector<String> allsentence) throws IOException
	 {
		
			
		 float add=(float)((1-alpha)*1.0/(float)allsentence.size());
		 File ff = new File(finalmatrix);  
		 BufferedWriter output = new BufferedWriter(new FileWriter(ff));		 				  	     
		 BufferedReader br = new BufferedReader(new FileReader(aftertranspose));
		 String str="";
		 while((str=br.readLine()) != null) 
		 {
			 StringTokenizer st = new StringTokenizer(str);
			 while(st.hasMoreElements())
			 {
				float temp= Float.parseFloat(st.nextElement().toString());
				float finalvalue=add+alpha*temp;
				output.write(Float.toString(finalvalue)+"  ");
			 }
			
			output.newLine();
			 
		 }
		 
		 br.close();
		 output.close();
	 }
	 
	 
	 public static ArrayList<Float> prvalue(Vector<String> allsentence) throws IOException
	 {
		
		
			
			ArrayList<Float> list=new ArrayList<Float> ();
			ArrayList<Float> list1=new ArrayList<Float> ();
			for(int i=0;i<allsentence.size();i++)
			{
				list.add(1f);
			}
		   
			for(int i=0;i<cycletime;i++)
			{
				 BufferedReader br = new BufferedReader(new FileReader(finalmatrix));
				 String str="";
				 while((str=br.readLine()) != null) 
				 {
					 int as=0;
					 float sum=0f;
					 StringTokenizer st = new StringTokenizer(str);
					 while(st.hasMoreTokens())
					 {
						 float temp= Float.parseFloat(st.nextToken().toString());
						 float mulit=temp*list.get(as);
						 as++;
						 sum+=mulit;
					 }
					 list1.add(sum);
				 }
				br.close();
				list.clear();
				
				
				for(int l=0;l<list1.size();l++)
				{
					list.add(list1.get(l));
				}
				
				list1.clear();
				
			}
			
			
			 File ff = new File(prvalue);  
			 BufferedWriter output = new BufferedWriter(new FileWriter(ff));
			 for(int i=0;i<list.size();i++)
			 {
				 output.write(Float.toString(list.get(i)));
				 output.newLine();
			 }
			 output.close();	 
			 
			 if(list.size()==allsentence.size())
			 {
				 return list;
			 }
			 else
			 {
				 return new ArrayList<Float>();
			 }

	 }
	
	
	

}

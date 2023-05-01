import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class IntSorter {

  public static class IntSorterMapper
       extends Mapper<Object, Text, IntWritable, IntWritable>{

    private IntWritable number = new IntWritable();

    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
        number.set(Integer.parseInt(value.toString()));
        context.write(number, new IntWritable(1));
    }
  }

  public static class IntSorterReducer
       extends Reducer<IntWritable,IntWritable,IntWritable,IntWritable> {

    public void reduce(IntWritable key, Iterable<IntWritable> values,
                       Context context
                       ) throws IOException, InterruptedException {
      for (IntWritable value : values) {
        context.write(key, value);
      }
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "int sort");
    job.setJarByClass(IntSorter.class);
    job.setMapperClass(IntSorterMapper.class);
    job.setReducerClass(IntSorterReducer.class);
    job.setOutputKeyClass(IntWritable.class);
    job.setOutputValueClass(IntWritable.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}

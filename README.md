# MapReduceFaultTolerance
The main focus of this experiment is to test the fault tolerance of a MapReduce framework. We aim at simulating an environment and results as close to the ones discussed in the paper https://static.googleusercontent.com/media/research.google.com/en//archive/mapreduce-osdi04.pdf

This experiment was conducted on a 2 node cluster set up with RHEL7. However, we can use more nodes or another distribution. The commands may vary for the latter.

# Hadoop Setup
All of these steps need to be run for all nodes as it is unless mentioned otherwise

### Disable the firewall
```
sudo systemctl stop firewalld 
sudo systemctl disable firewalld
```
### hadoop1 will be our masternode, hadoop2 (.. and so on) will be the slaves. Edit the /etc/hosts file to add an entry for all nodes

### Install Java & Hadoop
```
yum install java-1.8.0-openjdk -y
sudo dnf install java-1.8.0-openjdk-devel

mkdir /hadoop 
cd /hadoop/ 
sudo wget https://archive.apache.org/dist/hadoop/core/hadoop-3.1.1/hadoop-3.1.1.tar.gz
sudo tar -xzf hadoop-3.1.1.tar.gz
```

### Update environmental variables
```
sudo vi ~/.bashrc
```
Add the following lines
```
export HDFS_NAMENODE_USER="root"
export HDFS_DATANODE_USER="root" 
export HDFS_SECONDARYNAMENODE_USER="root" 
export YARN_RESOURCEMANAGER_USER="root" 
export YARN_NODEMANAGER_USER="root" 
export JAVA_HOME= { path to jre, run "readlink -f $(which java)" to get the path}
export PATH=$PATH:$JAVA_HOME/bin
export HADOOP_HOME=/hadoop/hadoop-3.1.1
```

### Configuration
Use the configuration files in the confg folder. Remember only core-site.xml files needs to be copied to both master and slave nodes. Rest all the files are only for master node. These files should be edited in the /hadoop/hadoop-3.1.1/etc/hadoop/ folder.

### Set up SSH 
```
ssh-keygen
sudo ssh-copy-id -i ~/.ssh/id_rsa.pub root@hadoop1 
sudo ssh-copy-id -i ~/.ssh/id_rsa.pub root@hadoop2
```

### Add slave nodes only on the master node
On the master node, add the slave nodes as workers
```
sudo vi /hadoop/hadoop-3.1.1/etc/hadoop/workers
{Add all the entries of slave node. In our case we will only add an entry for hadoop2}
```

### Format namenode only on master
```
sudo /hadoop/hadoop-3.1.1/bin/hdfs namenode -format
```

### Starting Hadoop
```
sudo /hadoop/hadoop-3.1.1/sbin/start-all.sh
```
You can verify all the slave nodes using the below command
```
sudo /hadoop/hadoop-3.1.1/bin/hdfs dfsadmin -report
```


# Data and Job Setup
Use the job/FileGenerator.java file to create a dummy dataset. You can adjust the size of the output file desired.

### Moving data to hdfs
```
sudo /hadoop/hadoop-3.1.1/bin/hdfs dfs -put integers.txt /some/path/integers.txt
```

### Jar setup
Copy the job/IntSorter.java file to a folder. Run below commands in the same folder
```
javac -classpath $HADOOP_HOME/share/hadoop/common/hadoop-common-3.1.1.jar:$HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-client-core-3.1.1.jar IntSorter.java
echo "Main-Class: IntSorter" > manifest.txt
jar cvfm IntSorter.jar manifest.txt .
```

### Run the job
```
/hadoop/hadoop-3.1.1/bin/hadoop jar IntSorter.jar IntSorter {input path} {output path}
```

# Testing Fault Tolerance
To test the fault tolerance of the MapReduce framework, one can simply shutdown any slave node(s) and can observe the data transfer rate and other statistics via the below Hadoop pages (change localhost to the masternode hostname) - 
- Hadoop NameNode: http://localhost:9870/
- Hadoop ResourceManager: http://localhost:8088/
- Hadoop JobHistoryServer: http://localhost:19888/

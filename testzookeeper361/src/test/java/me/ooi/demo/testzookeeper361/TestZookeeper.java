package me.ooi.demo.testzookeeper361;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

/**
 * @author jun.zhao
 * @since 1.0
 */
@TestInstance(Lifecycle.PER_CLASS)
public class TestZookeeper {
	
	private ZooKeeper zk;
	
	@BeforeAll
	private void init() throws IOException, InterruptedException {
		CountDownLatch counter = new CountDownLatch(1);
		
		zk = new ZooKeeper("localhost:2181", 1000*10, (WatchedEvent event)->{
			System.out.println("event->"+event);
	        if (event.getType() == Event.EventType.None) {
	            switch (event.getState()) {
		            case SyncConnected:
		            	counter.countDown();
		                break;
		            case Expired:
		            	throw new RuntimeException("connection time out");
					default:
						break;
		            }
	        }else if( event.getType() == Event.EventType.NodeDataChanged ) {
	        	String path = event.getPath();
	        	System.out.println("changed paths is "+path);
	        	
				try {
					Stat stat = zk.exists(path, false);
					byte[] data = zk.getData(path, false, stat);
		    		System.out.println("data->"+new String(data, Charset.forName("utf-8")));
				} catch (KeeperException | InterruptedException e) {
					e.printStackTrace();
				}
	        }
		});
		
		counter.await();
	}
	
	@AfterAll
	private void destroy() throws InterruptedException {
		if( zk != null ) {
			zk.close();
		}
	}
	
	@Test
	public void t2() throws InterruptedException, IOException {
		CountDownLatch cdl = new CountDownLatch(1);
		
		ZooKeeper zk = new ZooKeeper("localhost:2181", 1000*10, (WatchedEvent event)->{
			System.out.println("event->"+event);
	        if (event.getType() == Event.EventType.None) {
	            // We are are being told that the state of the
	            // connection has changed
	            switch (event.getState()) {
		            case SyncConnected:
		                // In this particular example we don't need to do anything
		                // here - watches are automatically re-registered with 
		                // server and any watches triggered while the client was 
		                // disconnected will be delivered (in order of course)
		            	cdl.countDown();
		                break;
		            case Expired:
		                // It's all over
		                cdl.countDown();
		                break;
					default:
						break;
		            }
	        } else {
//	            if (path != null && path.equals(znode)) {
//	                // Something has changed on the node, let's find out
//	                zk.exists(znode, true, this, null);
//	            }
	        }
		});
		
		cdl.await();
		
		zk.close();
		
		System.out.println("ok");
	}
	
	@Test
	public void t3() throws KeeperException, InterruptedException {
		System.out.println("zk=>>"+zk);
	}
	
	@Test
	public void t4() throws KeeperException, InterruptedException {
		
		//test exists
		String myTestPath = "/mytest";
		Stat stat = zk.exists(myTestPath, false);
		System.out.println(stat);
		if( stat == null ) {
			
			//test create
			String path = zk.create(myTestPath, "测试".getBytes(Charset.forName("utf-8")), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			System.out.println(path);
		}else {
			
			//test getData
			System.out.println(stat.getVersion());
			byte[] data = zk.getData(myTestPath, false, stat);
			System.out.println("data->"+new String(data, Charset.forName("utf-8")));
		}
	}
	
	@Test
	public void testSetData() throws KeeperException, InterruptedException {
		String myTestPath = "/mytest";
		Stat stat = zk.exists(myTestPath, false);
		System.out.println(stat);
		
		System.out.println(stat.getVersion());
		stat = zk.setData(myTestPath, "测试2".getBytes(Charset.forName("utf-8")), stat.getVersion());
		System.out.println(stat);
		
		System.out.println(stat.getVersion());
		byte[] data = zk.getData(myTestPath, false, stat);
		System.out.println("data->"+new String(data, Charset.forName("utf-8")));
	}
	
//	@Test
//	public void testSetData() throws KeeperException, InterruptedException {
//		String myTestPath = "/mytest";
//		Stat stat = zk.exists(myTestPath, false);
//		System.out.println(stat);
//		
//		System.out.println(stat.getVersion());
//		byte[] data = zk.getData(myTestPath, (WatchedEvent event)->{
//			System.out.println("event->"+event);
//		}, stat);
//		
//		System.out.println("data->"+new String(data, Charset.forName("utf-8")));
//	}
	
	@Test
	public void testWatch() throws KeeperException, InterruptedException {
		String myTestPath = "/mytest";
		Stat stat = zk.exists(myTestPath, true);
		zk.setData(myTestPath, "测试4".getBytes(Charset.forName("utf-8")), stat.getVersion());
	}
	
	@Test
	public void testWatch2() throws KeeperException, InterruptedException {
		String myTestPath = "/mytest";
		Stat stat = zk.exists(myTestPath, (event)->{
			System.out.println("event2->"+event);
			if( event.getType() == Event.EventType.NodeDataChanged ) {
	        	String path = event.getPath();
	        	System.out.println("changed paths is "+path);
	        	
				try {
					Stat stat2 = zk.exists(path, false);
					byte[] data = zk.getData(path, false, stat2);
		    		System.out.println("data->"+new String(data, Charset.forName("utf-8")));
				} catch (KeeperException | InterruptedException e) {
					e.printStackTrace();
				}
	        }
		});
		zk.setData(myTestPath, "测试5".getBytes(Charset.forName("utf-8")), stat.getVersion());
	}

}

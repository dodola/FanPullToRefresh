下拉刷新(安能饭否)
====
##Useage
###Layout xml
``` xml

	<com.fanpulltorefresh.PullToRefresh
		android:id="@+id/pullDownView1" android:layout_width="fill_parent"
		android:layout_height="wrap_content"
		>
		<!--content-->
		<ListView android:id="@+id/listview1" android:layout_width="fill_parent"
			android:layout_height="fill_parent"></ListView>
	</com.fanpulltorefresh.PullToRefresh>
```
###Activity
``` java
public class MainActivity extends Activity implements
		PullToRefresh.UpdateHandle {
	PullToRefresh refresh;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		refresh = (PullToRefresh) this.findViewById(R.id.pullDownView1);
		refresh.setUpdateHandle(this);
		ListView view = (ListView) this.findViewById(R.id.listview1);
		view.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, getData()));
	}

	private List<String> getData() {

		List<String> data = new ArrayList<String>();

		for (int i = 1; i < 30; i++) {
			data.add("测试数据" + i);
		}

		return data;
	}

	@Override
	public void onUpdate() {
		new MyThread(handler).start();
	}

	class MyThread extends Thread {
		private Handler handler;

		public MyThread(Handler handler) {
			this.handler = handler;

		}

		@Override
		public void run() {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			Message msg = handler.obtainMessage();
			handler.sendMessage(msg);
			super.run();
		}

	}

	private final Handler handler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(Message msg) { // 处理Message，更新ListView
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy年MM月dd日   HH:mm:ss     ");

			Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
			String str = formatter.format(curDate);
			refresh.endUpdate(str);
		}
	};
}
```
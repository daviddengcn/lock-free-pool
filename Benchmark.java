public class Benchmark {
  public static void main(String[] args) throws Exception {
    IFactory<Object[]> op = null;

		String cmd = args[0];
    final int cap = Integer.parseInt(args[1]);
    final int nThread = Integer.parseInt(args[2]);
    final int K = Integer.parseInt(args[3]);

    final IFactory<Object[]> factory = new IFactory<Object[]>() {
      @Override
      public Object[] alloc() {
        return new Object[K];
      }

      @Override
      public void free(Object[] t) {
      }
    };

    if (cmd.equals("sync")) {
      System.out.println("Testing SyncObjectPool...");
      op = new SynchronizedPool<>(cap, factory);
    } else if (cmd.equals("lockfree")) {
      System.out.println("Testing LockFreeObjectPool...");
      op = new LockFreePool<>(cap, factory);
    } else if (cmd.equals("nopool")) {
      System.out.println("Testing no object pool...");
      op = factory;
    } else {
      System.out.println("Unknown: " + cmd);
      return;
    }

    final int N = 1000000;

    // warm up JIT
    for (int i = 0; i < N; i++) {
      op.free(op.alloc());
    }
    Thread.sleep(1000);
    System.gc();

    long start = System.currentTimeMillis();

    final IFactory<Object[]> objectPool = op;

		Thread[] threads = new Thread[nThread];
		for (int i = 0; i < nThread; i++) {
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < N; i++) {
            Object[] e = objectPool.alloc();
						System.arraycopy(e, 0, e, 0, e.length);
            try {
              Thread.sleep(0);
            } catch (InterruptedException e1) {
              Thread.currentThread().interrupt();
            }
						objectPool.free(e);
					}
				}
			});
		}

		for (Thread thread: threads) {
			thread.start();
		}

		for (Thread thread: threads) {
			thread.join();
		}

		long elapsed = System.currentTimeMillis() - start;
		System.out.println("Total: " + elapsed);
	}
}


import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class LockFreePool<E> implements IFactory<E> {
  private final IFactory<E> factory;
  private final AtomicReferenceArray<E> objects;
  private final AtomicInteger cnt;

  public LockFreePool(int cap, IFactory<E> factory) {
    this.factory = factory;
    this.objects = new AtomicReferenceArray<>(cap);
    this.cnt = new AtomicInteger(0);
  }

  @Override
  public E alloc() {
  outloop:
    for (int i = 0; i < 30; i++) {
      int n;
      do {
        n = cnt.get();
        if (n == 0) {
          break outloop;
        }
      } while (!cnt.compareAndSet(n, n - 1));
      E e = objects.getAndSet(n - 1, null);
      if (e != null) {
        return e;
      }
    }
    return factory.alloc();
  }

  @Override
  public void free(E e) {
    outloop:
    for (int i = 0; i < 30; i++) {
      int n;
      do {
        n = cnt.get();
        if (n == objects.length()) {
          break outloop;
        }
      } while (!cnt.compareAndSet(n, n + 1));
      if (objects.compareAndSet(n, null, e)) {
        return;
      }
    }
  }
}

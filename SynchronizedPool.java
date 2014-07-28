public class SynchronizedPool<E> implements IFactory<E> {
  private final IFactory<E> factory;
  private final E[] objects;
  private int cnt;

  @SuppressWarnings("unchecked")
  public SynchronizedPool(int cap, IFactory<E> factory) {
    this.factory = factory;
    this.objects = (E[]) new Object[cap];
    this.cnt = 0;
  }

  @Override
  public synchronized E alloc() {
    if (cnt > 0) {
      cnt--;
      return this.objects[cnt];
    }
    return factory.alloc();
  }

  @Override
  public synchronized void free(E e) {
    if (cnt < this.objects.length) {
      this.objects[cnt] = e;
      cnt++;
      return;
    }

    factory.free(e);
  }

}

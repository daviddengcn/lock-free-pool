public interface IFactory<E> {
  E alloc();

  void free(E e);
}

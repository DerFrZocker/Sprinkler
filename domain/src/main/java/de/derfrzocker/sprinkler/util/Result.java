package de.derfrzocker.sprinkler.util;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class Result<S, E> {

    private final S success;
    private final E error;

    private Result(S success, E error) {
        if (success == null && error == null) {
            throw new IllegalArgumentException("Success and error cannot be both null");
        }
        if (success != null && error != null) {
            throw new IllegalArgumentException(String.format("""
                    Success and error cannot be both present.
                    Success: %s
                    Error: %s
                    """, success, error));
        }

        this.success = success;
        this.error = error;
    }

    public static <S, E> Result<S, E> success(S success) {
        return new Result<>(success, null);
    }

    public static <S, E> Result<S, E> error(E error) {
        return new Result<>(null, error);
    }

    public boolean isSuccess() {
        return success != null;
    }

    public boolean isError() {
        return error != null;
    }

    public S successOr(Supplier<S> or) {
        return success != null ? success : or.get();
    }

    public E errorOr(Supplier<E> or) {
        return error != null ? error : or.get();
    }

    public Optional<S> success() {
        return Optional.ofNullable(success);
    }

    public Optional<E> error() {
        return Optional.ofNullable(error);
    }

    public <T, F> Result<T, F> map(Function<S, T> mapSuccess, Function<E, F> mapError) {
        T newSuccess = null;
        if (isSuccess()) {
            newSuccess = mapSuccess.apply(success);
        }

        F newError = null;
        if (isError()) {
            newError = mapError.apply(error);
        }

        return new Result<>(newSuccess, newError);
    }
}

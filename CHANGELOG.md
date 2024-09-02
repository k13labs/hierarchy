This is a history of changes to k13labs/hierarchy

# 0.0.2 - 2024-09-02
* Removed default alter-var-root in `core` namespace, user code must now invoke `activate!` manually.
* Add utility functions `activate!` and `deactivate!` to alter the `derive` and `underive` var bindings.
* Add utility macro `hierarchy.core/bound` to redef the `derive` and `underive` var bindings at runtime.

# 0.0.1 - 2024-09-01
* The initial release.

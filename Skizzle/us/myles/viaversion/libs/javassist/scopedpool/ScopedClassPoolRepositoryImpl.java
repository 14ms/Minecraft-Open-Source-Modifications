/*
 * Decompiled with CFR 0.150.
 */
package us.myles.viaversion.libs.javassist.scopedpool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import us.myles.viaversion.libs.javassist.ClassPool;
import us.myles.viaversion.libs.javassist.LoaderClassPath;
import us.myles.viaversion.libs.javassist.scopedpool.ScopedClassPool;
import us.myles.viaversion.libs.javassist.scopedpool.ScopedClassPoolFactory;
import us.myles.viaversion.libs.javassist.scopedpool.ScopedClassPoolFactoryImpl;
import us.myles.viaversion.libs.javassist.scopedpool.ScopedClassPoolRepository;

public class ScopedClassPoolRepositoryImpl
implements ScopedClassPoolRepository {
    private static final ScopedClassPoolRepositoryImpl instance = new ScopedClassPoolRepositoryImpl();
    private boolean prune = true;
    boolean pruneWhenCached;
    protected Map<ClassLoader, ScopedClassPool> registeredCLs = Collections.synchronizedMap(new WeakHashMap());
    protected ClassPool classpool;
    protected ScopedClassPoolFactory factory = new ScopedClassPoolFactoryImpl();

    public static ScopedClassPoolRepository getInstance() {
        return instance;
    }

    private ScopedClassPoolRepositoryImpl() {
        this.classpool = ClassPool.getDefault();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        this.classpool.insertClassPath(new LoaderClassPath(cl));
    }

    @Override
    public boolean isPrune() {
        return this.prune;
    }

    @Override
    public void setPrune(boolean prune) {
        this.prune = prune;
    }

    @Override
    public ScopedClassPool createScopedClassPool(ClassLoader cl, ClassPool src) {
        return this.factory.create(cl, src, this);
    }

    @Override
    public ClassPool findClassPool(ClassLoader cl) {
        if (cl == null) {
            return this.registerClassLoader(ClassLoader.getSystemClassLoader());
        }
        return this.registerClassLoader(cl);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ClassPool registerClassLoader(ClassLoader ucl) {
        Map<ClassLoader, ScopedClassPool> map = this.registeredCLs;
        synchronized (map) {
            if (this.registeredCLs.containsKey(ucl)) {
                return this.registeredCLs.get(ucl);
            }
            ScopedClassPool pool = this.createScopedClassPool(ucl, this.classpool);
            this.registeredCLs.put(ucl, pool);
            return pool;
        }
    }

    @Override
    public Map<ClassLoader, ScopedClassPool> getRegisteredCLs() {
        this.clearUnregisteredClassLoaders();
        return this.registeredCLs;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clearUnregisteredClassLoaders() {
        ArrayList<ClassLoader> toUnregister = null;
        Map<ClassLoader, ScopedClassPool> map = this.registeredCLs;
        synchronized (map) {
            for (Map.Entry<ClassLoader, ScopedClassPool> reg : this.registeredCLs.entrySet()) {
                if (!reg.getValue().isUnloadedClassLoader()) continue;
                ClassLoader cl = reg.getValue().getClassLoader();
                if (cl != null) {
                    if (toUnregister == null) {
                        toUnregister = new ArrayList<ClassLoader>();
                    }
                    toUnregister.add(cl);
                }
                this.registeredCLs.remove(reg.getKey());
            }
            if (toUnregister != null) {
                for (ClassLoader cl : toUnregister) {
                    this.unregisterClassLoader(cl);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unregisterClassLoader(ClassLoader cl) {
        Map<ClassLoader, ScopedClassPool> map = this.registeredCLs;
        synchronized (map) {
            ScopedClassPool pool = this.registeredCLs.remove(cl);
            if (pool != null) {
                pool.close();
            }
        }
    }

    public void insertDelegate(ScopedClassPoolRepository delegate) {
    }

    @Override
    public void setClassPoolFactory(ScopedClassPoolFactory factory) {
        this.factory = factory;
    }

    @Override
    public ScopedClassPoolFactory getClassPoolFactory() {
        return this.factory;
    }
}


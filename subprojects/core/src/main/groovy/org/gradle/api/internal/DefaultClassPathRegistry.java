/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal;

import org.gradle.util.GFileUtils;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class DefaultClassPathRegistry implements ClassPathRegistry {
    private final List<ClassPathProvider> providers = new ArrayList<ClassPathProvider>();

    public DefaultClassPathRegistry(ClassPathProvider... providers) {
        this.providers.addAll(Arrays.asList(providers));
    }

    public ClassPath getClassPath(String name) {
        List<File> files = getClassPathFiles(name);
        return new DefaultClassPath(files);
    }

    private List<File> getClassPathFiles(String name) {
        for (ClassPathProvider provider : providers) {
            Set<File> classpath = provider.findClassPath(name);
            if (classpath != null) {
                return new ArrayList<File>(classpath);
            }
        }
        throw new IllegalArgumentException(String.format("unknown classpath '%s' requested.", name));
    }

    private static class DefaultClassPath implements ClassPath, Serializable {
        private final List<File> files;

        public DefaultClassPath(List<File> files) {
            this.files = files;
        }

        public Collection<URI> getAsURIs() {
            return GFileUtils.toURIs(files);
        }

        public Collection<File> getAsFiles() {
            return files;
        }

        public URL[] getAsURLArray() {
            return GFileUtils.toURLArray(files);
        }

        public Collection<URL> getAsURLs() {
            return GFileUtils.toURLs(files);
        }
    }
}

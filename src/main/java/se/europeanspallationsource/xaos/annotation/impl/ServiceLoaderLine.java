/*
 * Copyright 2018 European Spallation Source ERIC.
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
package se.europeanspallationsource.xaos.annotation.impl;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Set;
import java.util.SortedSet;


/**
 * One entry in a {@code META-INF/services/*} file. For purposes of collections,
 * all lines with the same implementing class are equal, and lines with lower
 * load {@code order} sort first (else by class name).
 *
 * @author claudio.rosati@esss.se
 * @see <a href="http://bits.netbeans.org/8.1/javadoc/org-openide-util-lookup/overview-summary.html">NetBeans Lookup API</a>
 */
@SuppressWarnings( "ClassWithoutLogger" )
class ServiceLoaderLine implements Comparable<ServiceLoaderLine> {

	static final String ORDER = "# end-of-order="; // NOI18N

	@SuppressWarnings( "NestedAssignment" )
	public static void parse( Reader r, SortedSet<ServiceLoaderLine> lines ) throws IOException {

		BufferedReader br = new BufferedReader(r);
		String line;
		String impl = null;
		int order = Integer.MAX_VALUE;

		while ( ( line = br.readLine() ) != null ) {
			if ( line.startsWith(ORDER) ) {
				order = Integer.parseInt(line.substring(ORDER.length()));
			} else {

				finalize(lines, impl, order);

				impl = line;
				order = Integer.MAX_VALUE;

			}
		}

		finalize(lines, impl, order);

	}

	private static void finalize( Set<ServiceLoaderLine> lines, String impl, int position ) {
		if ( impl != null ) {
			lines.add(new ServiceLoaderLine(impl, position));
		}
	}

	private final String impl;
	private final int order;

	public ServiceLoaderLine( String impl, int position ) {
		this.impl = impl;
		this.order = position;
	}

	@Override
	@SuppressWarnings( "AccessingNonPublicFieldOfAnotherObject" )
	public int compareTo( ServiceLoaderLine o ) {

		if ( impl.equals(o.impl) ) {
			return 0;
		}

		int difference = order - o.order;

		return difference != 0 ? difference : impl.compareTo(o.impl);

	}

	@Override
	@SuppressWarnings( "AccessingNonPublicFieldOfAnotherObject" )
	public boolean equals( Object o ) {
		return ( o instanceof ServiceLoaderLine ) && impl.equals(( (ServiceLoaderLine) o ).impl);
	}

	@Override
	public int hashCode() {
		return impl.hashCode();
	}

	public void write( PrintWriter w ) {

		w.println(impl);

		if ( order != Integer.MAX_VALUE ) {
			w.println(ORDER + order);
		}

	}

}

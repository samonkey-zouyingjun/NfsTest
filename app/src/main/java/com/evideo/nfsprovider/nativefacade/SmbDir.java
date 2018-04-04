/*
 * Copyright 2017 Google Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.evideo.nfsprovider.nativefacade;

import android.support.annotation.Nullable;

import com.evideo.nfsprovider.base.DirectoryEntry;

import java.io.Closeable;
import java.io.IOException;

public interface SmbDir extends Closeable {

  @Nullable
  DirectoryEntry readDir() throws IOException;

}

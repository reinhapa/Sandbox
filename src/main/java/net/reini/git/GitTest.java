/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016, 2025 Patrick Reinhart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.reini.git;

import static java.nio.file.Files.exists;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RmCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;

/**
 * @author patrick.reinhart
 */
public class GitTest {
  static final Pattern gitIgnorePattern = Pattern.compile("^.*(/|)\\.gitignore$");
  static final Pattern jazzIgnorePattern = Pattern.compile("^.*(/|)\\.jazzignore$");

  public static void main(String[] args) throws Exception {
    Path gitDir = Paths.get("/tmp/test");

    try (Git git = init(gitDir)) {
      // add all untracked files
      Status status = git.status().call();
      Set<String> toAdd = new HashSet<>();
      Set<String> toRemove = new HashSet<>();
      Set<String> toRestore = new HashSet<>();

      // go over untracked files
      for (String untracked : status.getUntracked()) {
        // add it to the index
        toAdd.add(untracked);
      }
      // go over modified files
      for (String modified : status.getModified()) {
        // adds a modified entry to the index
        toAdd.add(modified);
      }

      // go over all deleted files
      for (String removed : status.getMissing()) {
        System.out.println("-: " + removed);
        // adds a modified entry to the index
        if (gitIgnorePattern.matcher(removed).matches()) {
          // restore .gitignore files that where deleted
          toRestore.add(removed);
        } else {
          toRemove.add(removed);
        }
      }

      // write(gitDir.resolve("testfile"), "abc".getBytes());
      // toAdd.add("testfile");

      // delete(gitDir.resolve("testfile"));
      // toRemove.add("testfile");

      // execute the git index commands if needed
      if (!toAdd.isEmpty()) {
        AddCommand add = git.add();
        toAdd.forEach(add::addFilepattern);
        add.call();
      }
      if (!toRemove.isEmpty()) {
        RmCommand rm = git.rm();
        toRemove.forEach(rm::addFilepattern);
        rm.call();
      }
      if (!toRestore.isEmpty()) {
        CheckoutCommand checkout = git.checkout();
        toRestore.forEach(checkout::addPath);
        checkout.call();
      }

      // execute commit if something has changed
      if (!toAdd.isEmpty() || !toRemove.isEmpty()) {
        PersonIdent ident = new PersonIdent("Robocop", "john.doe@somewhere.com");
        git.commit().setMessage("auto commit").setAuthor(ident).setCommitter(ident).call();
      }
    }
  }

  private static Git init(Path gitDir) throws Exception {
    if (exists(gitDir)) {
      RepositoryBuilder repoBuilder = new RepositoryBuilder();
      repoBuilder.setGitDir(gitDir.resolve(".git").toFile());
      repoBuilder.readEnvironment();
      repoBuilder.findGitDir();
      Repository repo = repoBuilder.build();
      return new Git(repo);
    } else {
      return Git.init().setDirectory(gitDir.toFile()).call();
    }
  }

  interface ElementCallback {
    /**
     * @param gitRoot GIT root directory
     * @param removed <code>true</code> if element was removed, <code>false</code> otherwise
     * @param name the relative file name to the GIT directory
     * @return <code>true</code> to add/rm it the GIT index
     */
    boolean handleElement(Path gitRoot, boolean removed, String name);
  }
}

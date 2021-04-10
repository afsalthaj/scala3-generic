{ pkgs ? import <nixpkgs> {} }:
with import (builtins.fetchTarball {
  # Descriptive name to make the store path easier to identify
  name = "nixos-unstable-2019-10-04";
  # Commit hash for nixos-unstable as of Mon Sep 2 01:17:20 2019 -0400
  url = https://github.com/nixos/nixpkgs/archive/85b7d89892a4ea088d541a694addb2e363758e44.tar.gz;
  # Hash obtained using `nix-prefetch-url --unpack <url>`
  sha256 = "0wxv4jvv9gri8kjzijrxdd1ijprwbsgsnzmjk2n4yxi0j41jk2f6";
}) {};

stdenv.mkDerivation rec {
  name = "dev-scala";

  buildInputs = [
    jdk11
    sbt
    bloop
  ];
}
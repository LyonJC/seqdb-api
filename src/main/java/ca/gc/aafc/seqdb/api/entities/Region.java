/*
 * =====================================================================
 * Class:		Region.java
 * Package: 	ca.gc.aafc.seqdb.api.entities
 * 
 * 
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Agriculture and Agri-Food Canada http://www.agr.gc.ca/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * =====================================================================
 * 
 */
package ca.gc.aafc.seqdb.api.entities;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import lombok.Builder;

/**
 * The Class Region.
 */
@Entity
@Audited
@Table(name = "Regions", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "Name", "GroupID" }) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "SAGESDataCache")
public class Region implements RestrictedByGroup {

  private Integer regionId;

  /** The symbol. */
  private String symbol;

  private String name;

  /** The aliases. */
  private String aliases;

  /** The applicable organisms. */
  private String applicableOrganisms;

  private Group group;

  /**
   * Instantiates a new region.
   */
  public Region() {
    super();
  }

  /**
   * Instantiates a new region tag.
   *
   * @param name
   *          the name
   * @param description
   *          the description
   * @param group
   *          the group
   * @param lastModified
   *          the last modified
   * @param left
   *          the left
   * @param right
   *          the right
   * @param symbol
   *          the symbol
   * @param aliases
   *          the aliases
   * @param applicableOrganisms
   *          the applicable organisms
   */
  @Builder
  public Region(String name, String description, Group group, Timestamp lastModified, int left,
      int right, String symbol, String aliases, String applicableOrganisms) {
    this.group = group;
    this.symbol = symbol;
    this.aliases = aliases;
    this.applicableOrganisms = applicableOrganisms;
  }

  // Property accessors

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "RegionID")
  public Integer getRegionId() {
    return this.regionId;
  }

  public void setRegionId(Integer regionId) {
    this.regionId = regionId;
  }


  /**
   * Gets the symbol.
   *
   * @return the symbol
   */
  @NotNull
  @Size(max = 50)
  @Column(name = "Symbol")
  public String getSymbol() {
    return symbol;
  }

  /**
   * Sets the symbol.
   *
   * @param symbol
   *          the new symbol
   */
  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  @NotNull
  @Column(name = "Name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the aliases.
   *
   * @return the aliases
   */
  @Size(max = 255)
  @Column(name = "Aliases")
  public String getAliases() {
    return aliases;
  }

  /**
   * Sets the aliases.
   *
   * @param aliases
   *          the new aliases
   */
  public void setAliases(String aliases) {
    this.aliases = aliases;
  }

  /**
   * Gets the applicable organisms.
   *
   * @return the applicable organisms
   */
  @Size(max = 255)
  @Column(name = "ApplicableOrganisims")
  public String getApplicableOrganisms() {
    return applicableOrganisms;
  }

  /**
   * Sets the applicable organisms.
   *
   * @param applicableOrganisms
   *          the new applicable organisms
   */
  public void setApplicableOrganisms(String applicableOrganisms) {
    this.applicableOrganisms = applicableOrganisms;
  }

  /**
   * Gets the group.
   *
   * @return the group
   */
  @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
  @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
  @JoinColumn(name = "GroupID")
  public Group getGroup() {
    return this.group;
  }

  /**
   * Sets the group.
   *
   * @param group
   *          the new group
   */
  @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
  public void setGroup(Group group) {
    this.group = group;
  }

  @Override
  @Transient
  @JsonIgnore
  public Group getAccessGroup() {
    return getGroup();
  }

}

/*
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.thewaterwalker.springbootldapreact.entity;

import com.github.thewaterwalker.springbootldapreact.config.WebSecurityConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
@Secured({WebSecurityConfig.ROLE_APP_READONLY, WebSecurityConfig.ROLE_APP_USER})
public interface LineItemRepository extends JpaRepository<LineItem, Long> {
    Collection<LineItem> findByUserId(Long id);
    Collection<LineItem> findByUserIdIn(Set<Long> ids);
}

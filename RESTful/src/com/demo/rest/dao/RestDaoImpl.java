package com.demo.rest.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.demo.rest.domain.Action;
import com.demo.rest.domain.Actor;
import com.demo.rest.domain.Workflow;

public class RestDaoImpl implements RestDao {
	private JdbcTemplate jdbcTemplate;

	private NamedParameterJdbcTemplate namedJdbcTemplate;

	public NamedParameterJdbcTemplate getNamedJdbcTemplate() {
		return this.namedJdbcTemplate;
	}

	public void setNamedJdbcTemplate(
			NamedParameterJdbcTemplate namedJdbcTemplate) {
		this.namedJdbcTemplate = namedJdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public JdbcTemplate getJdbcTemplate() {
		return this.jdbcTemplate;
	}

	public void saveWorkflow(Workflow workflow) {
		String sql = "insert into properties (workflowID,domain,workflowType,runID,version,description) values (?,?,?,?,?,?)";
		this.jdbcTemplate.update(sql,
				new Object[] { workflow.getWorkflowID(), workflow.getDomain(),
						workflow.getWorkflowType(), workflow.getRunID(),
						workflow.getVersion(), workflow.getDescription() });
	}

	public void saveActor(Actor actor) {
		String sql = "insert into actor (name,workflowID,type,domain,version,status) values (?,?,?,?,?,?)";
		this.jdbcTemplate.update(
				sql,
				new Object[] { actor.getName(), actor.getWorkflowID(),
						actor.getType().ordinal(), actor.getDomain(),
						actor.getVersion(), actor.getStatus() });
	}

	public Workflow getWorkflow(String workflowID, String domain) {
		String sql = "select workflowID,domain,workflowType,runID,version,description from properties where workflowID = ? and domain = ?";
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Workflow workflow = new Workflow();
				workflow.setWorkflowID(rs.getString("workflowID"));
				workflow.setDomain(rs.getString("domain"));
				workflow.setWorkflowType(rs.getString("workflowType"));
				workflow.setRunID(rs.getString("runID"));
				workflow.setDescription(rs.getString("description"));
				workflow.setVersion(rs.getFloat("version"));
				return workflow;
			}
		};
		return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {
				String.valueOf(workflowID), String.valueOf(domain) });
	}

	public void deleteWorkflow(String workflowID) {

		String sql = "delete from properties where workflowID = ?";

		this.jdbcTemplate.update(sql,
				new Object[] { String.valueOf(workflowID) });

	}

	public void update(String sql) {

		this.jdbcTemplate.execute(sql);

	}

	public void saveAction(Action action) {
		String sql = "insert into action (name, type,method,parameters ) values (:name ,:type,:method ,:parameters)";

		SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(
				action);

		this.namedJdbcTemplate.update(sql, namedParameters);

	}

	public Action getAction(String name) {
		String sql = "select name,type,method,parameters from action where name = :name";
		SqlParameterSource namedParameters = new MapSqlParameterSource("name",
				name);

		RowMapper mapper = new RowMapper() {

			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Action action = new Action();
				action.setName(rs.getString("name"));
				action.setType(rs.getString("type"));
				action.setMethod(rs.getString("method"));
				action.setParameters(rs.getString("parameters"));
				return action;
			}
		};
		return this.namedJdbcTemplate.queryForObject(sql, namedParameters,
				mapper);

	}
}
